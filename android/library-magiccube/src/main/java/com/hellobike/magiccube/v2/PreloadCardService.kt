package com.hellobike.magiccube.v2

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.SparseArray
import com.hellobike.magiccube.LoadStyleParams
import com.hellobike.magiccube.StyleManager
import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.utils.EncryptUtils
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.ext.loge
import com.hellobike.magiccube.v2.js.IJsEngine
import com.hellobike.magiccube.v2.js.JSEngineInitializer
import com.hellobike.magiccube.v2.preload.*
import com.hellobike.magiccube.v2.reports.Codes
import com.hellobike.magiccube.v2.reports.Msgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.collections.ArrayList

internal object PreloadCardService {

    private var running: AtomicBoolean = AtomicBoolean(false)

    private var preloadUrls = LinkedList<String>()

    private val handler = Handler(Looper.getMainLooper())

    private val metaDataRepository: MetaDataRepository = MetaDataRepository()

    // 预加载
    fun preloads(urls: List<String>) {
        if (urls.isEmpty()) return

        synchronized(preloadUrls) {
            preloadUrls.addAll(urls)
        }

        if (running.compareAndSet(false, true)) {
            startPreload()
        }
    }

    private fun startPreload() {
        GlobalScope.launch(Dispatchers.IO) {

            while (preloadUrls.isNotEmpty() && running.get()) {

                val url = synchronized(preloadUrls) {
                    preloadUrls.removeFirst()
                }


                if (StyleModelCache.hasValidStyleModel(url)) { // 已经缓存
                    continue
                }

                val params = LoadStyleParams(url)
                StyleManager.loadStyleWithUrl(object : StyleManager.IOnLoadViewModelListener {

                    override fun getLoadParams(): LoadStyleParams = params

                    override fun loadCompleted(url: String, style: StyleModel) {
                    }

                    override fun loadFailure(url: String, errorCode: Int, message: String) {
                        loge("$url 缓存失败! errorCode: $errorCode $message")
                    }
                })
            }
            running.compareAndSet(true, false)
        }
    }


    fun preloadMetaDataList(
        requests: List<WKRequest>,
        timeout: Long,
        callback: (response: WKResponse<List<WKResponse<IMetaData>>>) -> Unit
    ) {
        if (requests.isEmpty()) {
            val response = WKResponse<List<WKResponse<IMetaData>>>()
            response.data = ArrayList()
            response.code = Codes.PRELOAD_ERROR
            response.message = "批量预加载 meta data 失败，requests 为空"
            loge(response.message)
            callback(response)
            return
        }

        val responses = SparseArray<WKResponse<IMetaData>>(requests.size)

        var hasTimeOut = false
        var timeoutId: String? = null
        if (timeout > 0) {
            timeoutId = watchTimeout(timeout) {
                hasTimeOut = true

                // 加载成功几个就返回几个
                val metas = ArrayList<WKResponse<IMetaData>>(responses.size())
                for (i in 0 until responses.size()) {
                    metas.add(responses.valueAt(i))
                }

                val response = WKResponse<List<WKResponse<IMetaData>>>()
                response.code = Codes.PRELOAD_TIMEOUT
                response.message = Msgs.PRELOAD_TIMEOUT
                response.data = metas
                loge(response.message)
                callback(response)
            }
        }

        val start = System.currentTimeMillis()
        requests.forEachIndexed { index, request ->
            preloadMetaData(request, 0) { response ->

                if (hasTimeOut) {
                    return@preloadMetaData
                }

                responses.put(index, response)

                if (responses.size() == requests.size) {

                    if (!timeoutId.isNullOrBlank()) cancelWatchTimeout(timeoutId)

//                    logd("批量预加载完成! ${System.currentTimeMillis() - start}")

                    val metas = ArrayList<WKResponse<IMetaData>>(responses.size())
                    for (i in 0 until responses.size()) {
                        metas.add(responses.valueAt(i))
                    }

                    val newResponse = WKResponse<List<WKResponse<IMetaData>>>()
                    newResponse.data = metas
                    newResponse.code = Codes.SUCCESS
                    callback(newResponse)
                }
            }
        }
    }

    fun preloadMetaData(
        request: WKRequest,
        timeout: Long,
        callback: (meta: WKResponse<IMetaData>) -> Unit
    ) {

        var hasTimeOut = false
        var timeoutId: String? = null
        if (timeout > 0) {
            timeoutId = watchTimeout(timeout) {
                hasTimeOut = true
                val response = WKResponse<IMetaData>()
                response.code = Codes.PRELOAD_TIMEOUT
                response.message = Msgs.PRELOAD_TIMEOUT
                response.request = request
                response.data = null
                loge(response.message)
                callback(response)
            }
        }

//        logd("开始预加载: ${request.url}")

        val meteData = findMetaDataFromMemory(request.unitId)
        if (meteData != null && meteData.hasSuccess()) {
            if (hasTimeOut) {
                return
            }
            if (!timeoutId.isNullOrBlank()) cancelWatchTimeout(timeoutId)

//            logd("预加载读缓存成功: ${meteData.getUrl()}")

            val response = WKResponse<IMetaData>()
            response.code = Codes.SUCCESS
            response.request = request
            response.data = meteData
            callback(response)
            return
        }


        val cardService = CardService(null)
        val initializer = JSEngineInitializer()

        val dataSource = Data(request.data)

        initializer.installData(dataSource)
        cardService.loadStyleV2(
            request.url,
            initializer,
            { style: StyleModel, jsEngine: IJsEngine, newData: Map<String, Any?>? ->

                if (hasTimeOut) {
                    return@loadStyleV2
                }

                if (!timeoutId.isNullOrBlank()) cancelWatchTimeout(timeoutId)

                val metaData = WKMetaData(request)
                metaData.code = Codes.SUCCESS
                metaData.message = ""
                metaData.style = style
                metaData.jsEngine = jsEngine
//                logd("预加载成功: ${style.styleUrl}")

                if (!newData.isNullOrEmpty()) {
                    metaData.getData().clear()
                    metaData.getData().putAll(newData)
                }

                if (request.cacheToMemory) { // 需要注入到缓存
                    saveMetaDataToMemory(request.unitId, metaData)
                }


                val response = WKResponse<IMetaData>()
                response.code = Codes.SUCCESS
                response.request = request
                response.data = metaData
                callback(response)
            },
            { url: String, code: Int, message: String? ->

                if (hasTimeOut) {
                    return@loadStyleV2
                }
                if (!timeoutId.isNullOrBlank()) cancelWatchTimeout(timeoutId)

                val response = WKResponse<IMetaData>()
                response.code = code
                response.request = request
                response.message = message ?: "预加载出现，未知错误"
                response.data = null

//                loge("预加载失败[${response.message}] $url ")
                callback(response)
            })
    }

    private fun watchTimeout(timeout: Long, callback: Runnable): String {
        val id = UUID.randomUUID().toString()
        val message = Message.obtain(handler, callback)
        message.what = id.hashCode()
        handler.sendMessageDelayed(message, timeout)
        return id
    }


    private fun cancelWatchTimeout(id: String) {
//        handler.removeCallbacksAndMessages(id)
        handler.removeMessages(id.hashCode())
    }


    fun preloadToMemory(
        requests: List<WKRequest>,
        callback: ((response: WKResponse<List<WKResponse<IMetaData>>>) -> Unit)? = null
    ) {
        preloadMetaDataList(requests, -1) { response ->
            response.data?.forEach {
                val metaData = it.data
                val key = it.request?.unitId
                if (it.hasSuccess()) {
                    saveMetaDataToMemory(key, metaData)
                }
            }
            callback?.invoke(response)
        }
    }

    private fun generateUniqueId(url: String?, data: Map<String, Any?>?): String? {
        if (url.isNullOrBlank() || data.isNullOrEmpty()) return null
        try {
            return EncryptUtils.encryptMD5ToString(data.toString(), url)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return null
    }

    fun findMetaDataFromMemory(url: String?, data: Map<String, Any?>?): IMetaData? {
        val key = generateUniqueId(url, data)
        if (key.isNullOrBlank()) return null
        return metaDataRepository.getValue(key)
    }

    fun getMetaDataFromMemoryOnce(url: String?, data: Map<String, Any?>?): IMetaData? {
        val key = generateUniqueId(url, data)
        if (key.isNullOrBlank()) return null
        return metaDataRepository.removeKey(key)
    }

    private fun findMetaDataFromMemory(key: String?): IMetaData? {
        if (key.isNullOrBlank()) return null
        return metaDataRepository.getValue(key)
    }

    private fun saveMetaDataToMemory(key: String?, metaData: IMetaData?) {
        if (key.isNullOrBlank()) return
        if (metaData == null || !metaData.hasSuccess()) return
        metaDataRepository.saveValue(key, metaData)
    }
}