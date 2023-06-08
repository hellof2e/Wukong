package com.hellobike.magiccube.demo.preview.controller

import android.os.Handler
import android.os.Looper
import com.hellobike.magiccube.demo.CoroutineSupport
import com.hellobike.magiccube.utils.EncryptUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception
import java.lang.ref.WeakReference
import java.util.*

class PreviewController(callback: IPreviewCallback) {

    companion object {
        const val STYLE_FILE_NAME = "style.json"
        const val DATA_FILE_NAME = "data.json"
        const val DURATION = 1500L
    }

    private var isRequesting = false

    private val coroutine: CoroutineSupport by lazy { CoroutineSupport() }

    private val previewCallback: WeakReference<IPreviewCallback> = WeakReference(callback)

    private val client by lazy { OkHttpClient.Builder().build() }

    private var url: String? = null

    private val handler: Handler = Handler(Looper.getMainLooper())

    fun getStyleUrl(styleJson: String): String {
        if (url.isNullOrBlank()) return ""
        val md5 = EncryptUtils.encryptMD5ToString(styleJson)
        return "$url/$STYLE_FILE_NAME?md5=$md5&logic=1"
    }

    fun previewOnce(url: String) {
        if (previewCallback.get() == null) return
        this.url = url
        handler.removeCallbacksAndMessages(null)
        doPreview()
    }

    fun startPreview(url: String) {
        if (previewCallback.get() == null) return
        this.url = url

        handler.removeCallbacksAndMessages(null)

        doPreview()
        livePreview(url)
    }

    private fun livePreview(url: String) {
        handler.postDelayed({
            doPreview()
            livePreview(url)
        }, DURATION)
    }

    public fun stopPreview() {
        handler.removeCallbacksAndMessages(null)
    }

    private fun doPreview() {

        val baseUrl = url ?: return

        val styleUrl = "$baseUrl/$STYLE_FILE_NAME"
        val dataUrl = "$baseUrl/$DATA_FILE_NAME"


        if (isRequesting) return
        isRequesting = true

        coroutine.launch(Dispatchers.IO) {
            try {
                val styleJson = doRequest(styleUrl)
                val dataJson = doRequest(dataUrl)

                withContext(Dispatchers.Main) {
                    if (styleJson.isNullOrBlank()) {
                        previewCallback.get()?.onFailure(Exception("style.json 文件加载失败!!"))
                        return@withContext
                    }
                    if (dataJson.isNullOrBlank()) {
                        previewCallback.get()?.onFailure(Exception("data.json 文件加载失败!!"))
                        return@withContext
                    }

                    previewCallback.get()?.onSuccess(styleJson, dataJson)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    previewCallback.get()?.onFailure(e)
                }
            } finally {
                withContext(Dispatchers.Main) {
                    isRequesting = false
                }
            }
        }
    }

    private fun doRequest(url: String): String? {
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val input = response.body()?.byteStream()
        val bytes = input?.readBytes() ?: return null
        return String(bytes)
    }


}