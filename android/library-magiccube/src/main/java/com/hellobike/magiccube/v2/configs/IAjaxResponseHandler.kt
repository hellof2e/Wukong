package com.hellobike.magiccube.v2.configs

import org.json.JSONObject

interface IAjaxResponseHandler {

    fun handleResponse(response: JSONObject)

    fun handleError(code: Int, message: String)
}