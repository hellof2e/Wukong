package com.hellobike.magiccube.v2

import com.hellobike.magiccube.LoadStyleParams
import com.hellobike.magiccube.StyleManager
import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.v2.ext.loge
import com.hellobike.magiccube.v2.ext.logt
import com.hellobike.magiccube.v2.js.IJsEngine
import com.hellobike.magiccube.v2.js.JSEngineInitializer
import com.hellobike.magiccube.v2.js.MainJSEngine
import com.hellobike.magiccube.v2.reports.Codes

internal class CardService(private val cardContext: CardContext?) {

    private val taskQueue = AsyncTaskQueue()


    /**
     * @param onFailure: code[ DSLParser.ErrorCode ]
     * INVALID_STYLE: Context == null 或者 DownloadManager 下载成功，但是在生成StyleModel时候抛出异常
     *
     * INVALID_URL: url 是空的时候回调
     * VERSION_NOT_SUPPORT: MD5 校验成功 version 校验失败
     * INVALID_STYLE: json 是空
     * DOWNLOAD_FAILURE: MD5 校验失败，或者抛异常
     * DOWNLOAD_CANCEL: 下载任务被取消
     */
    fun loadStyleV2(
        url: String,
        jsEngineInitializer: JSEngineInitializer,
        onComplete: (style: StyleModel, jsEngine: IJsEngine, newData: Map<String, Any?>?) -> Unit,
        onFailure: (url: String, code: Int, message: String?) -> Unit
    ) {
        // 优先查找内存
        val styleModel = StyleModelCache.getStyleModelFromCache(url)
        val viewModel = styleModel?.layoutModel
        if (styleModel?.isValidViewModel() == true && viewModel != null) {
            onLoadMemSuccess(styleModel, viewModel, url, jsEngineInitializer, onComplete, onFailure)
            return
        }

        // 内存没有，异步加载并解析 ViewModel
        taskQueue.clear() // 首先清空之前积压的任务队列
        val currentTime = System.currentTimeMillis() // 根据主线程调用事件来顺序回调
        taskQueue.exec {
            // 加载样式
            val params = LoadStyleParams(url)
            params.id = currentTime
            StyleManager.loadStyleWithUrl(object : StyleManager.IOnLoadViewModelListener {

                override fun getLoadParams(): LoadStyleParams = params

                override fun loadCompleted(url: String, style: StyleModel) {
                    onLoadCompleted(url, jsEngineInitializer, style, onComplete, onFailure)
                }

                override fun loadFailure(url: String, errorCode: Int, message: String) {
                    onLoadFailed(url, errorCode, message, onFailure)
                }
            })
        }
    }


    // 内存缓存加载成功
    private fun onLoadMemSuccess(
        styleModel: StyleModel,
        viewModel: LayoutViewModel,
        url: String,
        jsEngineInitializer: JSEngineInitializer,
        onComplete: (style: StyleModel, jsEngine: IJsEngine, newData: Map<String, Any?>?) -> Unit,
        onFailure: (url: String, code: Int, message: String?) -> Unit
    ) {
        styleModel.fromCache = true
        jsEngineInitializer.installLogic(viewModel.logic)
        if (!viewModel.enableJSLifecycle()) { // 没有启用，直接同步执行
            handleDisableLifecycle(styleModel, url, jsEngineInitializer, onComplete, onFailure)
        } else { // 启用了就异步加载
            handleEnableLifecycle(styleModel, url, jsEngineInitializer, onComplete, onFailure)
        }
    }

    // 样式加载失败
    private fun onLoadFailed(
        url: String,
        errorCode: Int,
        message: String?,
        onFailure: (url: String, code: Int, message: String?) -> Unit
    ) {
        val cardContext = cardContext
        if (cardContext != null) {
            cardContext.runOnUiThread {
                StyleModelCache.removeStyleModel(url)
                onFailure.invoke(url, errorCode, message ?: "样式加载失败")
            }
        } else {
            UIUtils.runOnUiThread {
                StyleModelCache.removeStyleModel(url)
                onFailure.invoke(url, errorCode, message ?: "样式加载失败")
            }
        }
    }

    // 样式加载成功
    private fun onLoadCompleted(
        url: String,
        jsEngineInitializer: JSEngineInitializer,
        style: StyleModel,
        onComplete: (style: StyleModel, jsEngine: IJsEngine, newData: Map<String, Any?>?) -> Unit,
        onFailure: (url: String, code: Int, message: String?) -> Unit
    ) {
        val viewModel = style.layoutModel
        if (viewModel == null) {
            onLoadFailed(url, Codes.ERROR_PARSE_VIEW_MODEL, "ViewModel 获取失败!", onFailure)
            return
        }
        jsEngineInitializer.installLogic(viewModel.logic)

        if (viewModel.enableJSLifecycle()) {
            handleEnableLifecycle(style, url, jsEngineInitializer, onComplete, onFailure)
        } else {
            handleDisableLifecycle(style, url, jsEngineInitializer, onComplete, onFailure)
        }
    }


    // 禁处理用 js lifecycle，不需要前置执行js引擎
    private fun handleDisableLifecycle(
        styleModel: StyleModel,
        url: String,
        jsEngineInitializer: JSEngineInitializer,
        onComplete: (style: StyleModel, jsEngine: IJsEngine, newData: Map<String, Any?>?) -> Unit,
        onFailure: (url: String, code: Int, message: String?) -> Unit
    ) {
        var throwable: Throwable? = null
        val jsEngine = try {
            jsEngineInitializer.load(false)
        } catch (t: Throwable) {
            t.printStackTrace()
            throwable = t
            null
        }

        if (jsEngine != null) {
            val  cardContext = cardContext
            if (cardContext != null) {
                cardContext.runOnUiThread { onComplete.invoke(styleModel, jsEngine, null) }
            } else {
                UIUtils.runOnUiThread { onComplete.invoke(styleModel, jsEngine, null) }
            }
        } else {
            val code = Codes.ERROR_JS_LOAD_FAILED
            val msg = throwable?.toString() ?: "加载js引擎失败"
            onLoadFailed(url, code, msg, onFailure)
        }
    }

    // 处理启用 js lifecycle 的逻辑，需要前置只执行js
    private fun handleEnableLifecycle(
        styleModel: StyleModel,
        url: String,
        jsEngineInitializer: JSEngineInitializer,
        onComplete: (style: StyleModel, jsEngine: IJsEngine, newData: Map<String, Any?>?) -> Unit,
        onFailure: (url: String, code: Int, message: String?) -> Unit
    ) = MainJSEngine.postJSQueue {
        try {
            val jsEngine = jsEngineInitializer.load(true)
            val jsDataMap = jsEngine.callBeforeCreate()
            // 数据发生更改，则需要把新数据通知出去，数据没有发生改变，不需要回调
            val newData = try {
                if (jsDataMap.isNullOrEmpty()
                    || jsDataMap == jsEngineInitializer.getData()?.originData()
                ) null
                else jsDataMap
            } catch (t: Throwable) {
                t.printStackTrace()
                logt(t)
                jsDataMap
            }
            val cardContext = cardContext
            if (cardContext != null) {
                cardContext.runOnUiThread { onComplete.invoke(styleModel, jsEngine, newData) }
            } else {
                UIUtils.runOnUiThread { onComplete.invoke(styleModel, jsEngine, newData) }
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            val code = Codes.ERROR_JS_LOAD_FAILED
            val msg = t.toString()
            onLoadFailed(url, code, msg, onFailure)
        }
    }
}