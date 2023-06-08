package com.hellobike.magiccube.v2.configs

interface IDownloadResultHandler {

    fun handleSuccess(url: String, json: String)

    fun handleError(url: String, code: Int, message: String)
}