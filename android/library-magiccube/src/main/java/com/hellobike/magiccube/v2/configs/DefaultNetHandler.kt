package com.hellobike.magiccube.v2.configs

import com.hellobike.magiccube.net.FileDownloader
import com.hellobike.magiccube.net.NetCore
import com.hellobike.magiccube.v2.ext.logd
import com.hellobike.magiccube.v2.ext.loge
import com.hellobike.magiccube.v2.reports.Codes
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DefaultNetHandler : INetHandler {

    private val downloader by lazy { FileDownloader() }

    private val apiService: NetCore.ApiService by lazy { NetCore.createApiService() }

    override fun download(url: String, handler: IDownloadResultHandler) {
        downloader.download(url, object : FileDownloader.IDownloadListener {
            override fun onSuccess(url: String, json: String) {
                handler.handleSuccess(url, json)
            }

            override fun onError(url: String, code: Int, message: String) {
                handler.handleError(url, code, message)
            }
        })
    }

    override fun ajaxPost(url: String, data: HashMap<String, Any?>, handler: IAjaxResponseHandler) {
        apiService.postRequest(url, data).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    val json = JSONObject(body)
                    logd("------ 请求成功 ------")
                    logd(body)
                    handler.handleResponse(json)
                } else {
                    loge("网络请求异常! [${response.code()}, ${response.message()}]")
                    handler.handleError(response.code(), response.message())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                t.printStackTrace()
                loge("网络请求异常! [$t}]")
                handler.handleError(Codes.AJAX_ERROR, t.toString())
            }
        })
    }

    override fun ajaxGet(url: String, data: HashMap<String, Any?>, handler: IAjaxResponseHandler) {
        apiService.getRequest(url, data).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    handler.handleResponse(JSONObject(body))
                } else {
                    handler.handleError(response.code(), response.message())
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                handler.handleError(Codes.AJAX_ERROR, t.toString())
            }

        })
    }

    override fun ajax(
        url: String,
        type: String,
        data: HashMap<String, Any?>,
        handler: IAjaxResponseHandler
    ) {
        handler.handleError(Codes.AJAX_ERROR, "不支持的请求方式: $type")
    }
}