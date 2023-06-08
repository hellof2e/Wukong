package com.hellobike.magiccube.v2.preload

import com.hellobike.magiccube.parser.DSLParser
import com.hellobike.magiccube.v2.reports.Codes

class WKResponse<T> {

    var code: Int = Codes.SUCCESS

    var message: String = ""

    var data: T? = null

    var request: WKRequest? = null

    fun hasSuccess(): Boolean = code == Codes.SUCCESS && data != null

}