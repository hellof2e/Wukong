package com.hellobike.magiccube.v2.configs

interface INetHandler {

    fun download(url: String, handler: IDownloadResultHandler)

    fun ajaxPost(url: String, data: HashMap<String, Any?>, handler: IAjaxResponseHandler)

    fun ajaxGet(url: String, data: HashMap<String, Any?>, handler: IAjaxResponseHandler)

    fun ajax(url: String, type: String, data: HashMap<String, Any?>, handler: IAjaxResponseHandler)
}