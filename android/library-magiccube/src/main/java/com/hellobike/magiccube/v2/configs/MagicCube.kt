package com.hellobike.magiccube.v2.configs

import com.hellobike.magiccube.StyleManager
import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.parser.DSLParser
import com.hellobike.magiccube.parser.DSLParser.parseValue
import com.hellobike.magiccube.parser.engine.YogaEngine
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.v2.CardService
import com.hellobike.magiccube.v2.PreloadCardService
import com.hellobike.magiccube.v2.ext.loge
import com.hellobike.magiccube.v2.js.IJsEngine
import com.hellobike.magiccube.v2.js.JSEngine
import com.hellobike.magiccube.v2.js.JSEngineInitializer
import com.hellobike.magiccube.v2.js.MainJSEngine
import com.hellobike.magiccube.v2.preload.IMetaData
import com.hellobike.magiccube.v2.preload.WKRequest
import com.hellobike.magiccube.v2.preload.WKResponse

object MagicCube {

    internal lateinit var starter: MagicStarer

    fun doInit(starter: MagicStarer) {
        this.starter = starter
        val applicationContext = starter.context.applicationContext
        StyleManager.context = applicationContext
        DSLParser.density = starter.context.resources.displayMetrics.density
        DSLParser.scaleDensity = starter.context.resources.displayMetrics.scaledDensity
        DSLParser.widthScale = starter.context.resources.displayMetrics.widthPixels / 750f
        StyleManager.context = starter.context
        DSLParser.openWebBlock = { starter.clickHandler?.onCardClick(it) }
        YogaEngine.initEngine(applicationContext)
        UIUtils.runOnUiThread {
            JSEngine.INSTANCE.init()
            MainJSEngine.INSTANCE.initGlobalBridges()
        }
    }


    fun preloads(urls: List<String>) {
        if (StyleManager.context == null) {
            loge("还没有初始化，请初始化！【 MagicCube.doInit(starter: MagicStarer) 】")
            return
        }
        PreloadCardService.preloads(urls)
    }

    fun preloadMetaDataList(
        requests: List<WKRequest>,
        timeout: Long = 500,
        callback: (metas: WKResponse<List<WKResponse<IMetaData>>>) -> Unit
    ) {
        PreloadCardService.preloadMetaDataList(requests, timeout, callback)
    }

    fun preloadMetaData(
        request: WKRequest,
        timeout: Long = 500,
        callback: (meta: WKResponse<IMetaData>) -> Unit
    ) {
        PreloadCardService.preloadMetaData(request, timeout, callback)
    }

    fun preloadMetaDataToMemory(
        requests: List<WKRequest>,
        callback: ((response: WKResponse<List<WKResponse<IMetaData>>>) -> Unit)? = null
    ) {
        PreloadCardService.preloadToMemory(requests, callback)
    }

    fun findMetaDataFromMemory(url: String?, data: Map<String, Any?>?): IMetaData? {
        return PreloadCardService.findMetaDataFromMemory(url, data)
    }

    fun getMetaDataFromMemoryOnce(url: String?, data: Map<String, Any?>?): IMetaData? {
        return PreloadCardService.getMetaDataFromMemoryOnce(url, data)
    }

    /**
     * 将 rpx 和 px 根据屏幕比例进行适配计算
     * @param value 仅支持 "rpx" or "px"
     * @return 返回计算结果的像素数值
     */
    fun parseValue(value: String): Float {
        if (!value.endsWith("rpx") && !value.endsWith("px")) {
            throw IllegalArgumentException("仅仅支持 rpx 和 px 的等比转换")
        }
        return value.parseValue().value
    }
}