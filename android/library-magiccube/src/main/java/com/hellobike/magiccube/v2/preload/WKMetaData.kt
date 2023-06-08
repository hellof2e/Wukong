package com.hellobike.magiccube.v2.preload

import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.parser.DSLParser
import com.hellobike.magiccube.v2.js.IJsEngine
import com.hellobike.magiccube.v2.reports.Codes

class WKMetaData(request: WKRequest) : IMetaData {

    internal var code: Int = Codes.SUCCESS

    internal var message: String = "未知错误"

    internal var jsEngine: IJsEngine? = null

    internal var style: StyleModel? = null

    private val styleUrl: String = request.url

    private val styleData: HashMap<String, Any?> = request.data

    override fun getJS(): IJsEngine? = jsEngine

    override fun getStyle(): StyleModel? = style

    override fun getCode(): Int = code

    override fun getMsg(): String = message

    override fun hasSuccess(): Boolean = code == Codes.SUCCESS

    override fun getUrl(): String = styleUrl

    override fun getData(): HashMap<String, Any?> = styleData
}