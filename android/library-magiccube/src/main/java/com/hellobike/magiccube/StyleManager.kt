package com.hellobike.magiccube

import android.annotation.SuppressLint
import android.content.Context
import com.hellobike.magiccube.cache.StyleCache
import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.v2.StyleModelCache
import com.hellobike.magiccube.v2.configs.IDownloadResultHandler
import com.hellobike.magiccube.v2.configs.MagicCube
import com.hellobike.magiccube.v2.reports.Codes
import java.util.concurrent.ConcurrentHashMap


@SuppressLint("StaticFieldLeak")
object StyleManager {

    var context: Context? = null

    private val styleCache: StyleCache? by lazy {
        if(context==null)
            null
        val styleCache = StyleCache(context!!)
        styleCache.initWithDiskPath(null)
        styleCache.initLruCache()
        styleCache
    }


    internal fun injectToMemory(url: String, style: StyleModel) {
        styleCache?.injectToMemory(url, style.styleString)
    }

    fun removeStyleCache(url: String?) {
        if(url.isNullOrBlank()) return
        styleCache?.cleanWithUrl(url)
    }

    private val onLoadStyleListeners: ConcurrentHashMap<String, MutableList<IOnLoadViewModelListener>> by lazy { ConcurrentHashMap() }


    private fun onLoadStyleSuccess(params: LoadStyleParams, styleModel: StyleModel) {

        var throwable: Throwable? = null

        val viewModel = try {
            styleModel.parseViewModel()
        } catch (t: Throwable) {
            t.printStackTrace()
            throwable = t
            null
        }

        if (viewModel != null) {
            styleModel.layoutModel = viewModel
            StyleModelCache.cacheStyleModel(params.url, styleModel) // 缓存 ViewModel

            // 加载成功
            val list = synchronized(onLoadStyleListeners) { onLoadStyleListeners.remove(params.url) }
            list?.forEach {
                it.loadCompleted(params.url, styleModel)
            }

        } else {
            val code = Codes.ERROR_PARSE_VIEW_MODEL
            val msg = throwable?.message ?: "ViewModel 解析失败"

            // 解析失败
            val list = synchronized(onLoadStyleListeners) { onLoadStyleListeners.remove(params.url) }
            list?.forEach { it.loadFailure(params.url, code, msg) }
        }
    }

    fun loadStyleWithUrl(loadListener: IOnLoadViewModelListener) {
        val url = loadListener.getLoadParams().url
        val styleCache = this.styleCache
        if (context == null || styleCache == null) {
            loadListener.loadFailure(
                url,
                Codes.INVALID_STYLE,
                "context == null or styleCache == null"
            )
            return
        }

        synchronized(onLoadStyleListeners) {
            val list = onLoadStyleListeners[url] ?: mutableListOf()
            list.add(loadListener)
            if (onLoadStyleListeners[url] != null) {
                list.sortWith { o1, o2 -> (o1.getLoadParams().id - o2.getLoadParams().id).toInt() }
                return
            } else {
                onLoadStyleListeners[url] = list
            }
        }

        val cachedStyleModel = StyleModelCache.getStyleModelFromCache(url)
        val viewModel = cachedStyleModel?.layoutModel
        if (cachedStyleModel?.isValidViewModel() == true && viewModel != null) {
            cachedStyleModel.fromCache = true
            val list = synchronized(onLoadStyleListeners) { onLoadStyleListeners.remove(url) }
            list?.forEach {
                it.loadCompleted(url, cachedStyleModel)
            }
            return
        }

        //从Cache中获取
        val style = styleCache.queryStyleWithUrl(url)
        if(!style.isNullOrBlank()) {
            val styleModel = genStyleModel(url, style)
            styleModel.fromCache = true
            styleModel.styleUrl = url
            onLoadStyleSuccess(loadListener.getLoadParams(), styleModel)
        } else {
            //下载并缓存逻辑
            MagicCube.starter.net?.download(url, object : IDownloadResultHandler {

                override fun handleSuccess(url: String, styleStr: String) {
                    var throwable: Throwable? = null
                    val styleModel = try {
                        val styleModel = genStyleModel(url, styleStr)
                        styleCache.storeStyle(styleModel, url)
                        styleModel.styleUrl = url
                        styleModel.fromCache = false
                        styleModel
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        throwable = t
                        null
                    }

                    if (styleModel?.isValid() == true) {
                        onLoadStyleSuccess(loadListener.getLoadParams(), styleModel)
                    } else {
                        val code = Codes.INVALID_STYLE
                        val msg = throwable?.message ?: "StyleModel 无效"
                        val list =
                            synchronized(onLoadStyleListeners) { onLoadStyleListeners.remove(url) }
                        list?.forEach { it.loadFailure(url, code, msg) }
                    }
                }

                override fun handleError(url: String, code: Int, message: String) {
                    val list =
                        synchronized(onLoadStyleListeners) { onLoadStyleListeners.remove(url) }
                    list?.forEach { it.loadFailure(url, code, message) }
                }
            })
        }
    }

    interface IOnLoadViewModelListener {
        fun getLoadParams(): LoadStyleParams
        fun loadCompleted(url: String, style: StyleModel)
        fun loadFailure(url: String, errorCode: Int, message: String)
    }

    fun genStyleModel(url: String, style: String): StyleModel {
        val genStyle = StyleModel()
        genStyle.styleString = style
        return genStyle
    }
}