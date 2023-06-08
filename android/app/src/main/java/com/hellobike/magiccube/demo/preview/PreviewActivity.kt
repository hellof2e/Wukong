package com.hellobike.magiccube.demo.preview

import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.TypeReference
import com.hellobike.magiccube.R
import com.hellobike.magiccube.demo.logcat.WSLog
import com.hellobike.magiccube.demo.logd
import com.hellobike.magiccube.demo.loge
import com.hellobike.magiccube.demo.preview.controller.IPreviewCallback
import com.hellobike.magiccube.demo.preview.controller.PreviewCache
import com.hellobike.magiccube.demo.preview.controller.PreviewController
import com.hellobike.magiccube.loader.WKLoaderCenter
import com.hellobike.magiccube.loader.WKLoaderParam
import com.hellobike.magiccube.loader.insert.IWKInsert
import com.hellobike.magiccube.loader.insert.IWKInsertDialogListener
import com.hellobike.magiccube.loader.insert.IWKInsertLoaderListener
import com.hellobike.magiccube.v2.OnLoadStateListener
import com.hellobike.magiccube.v2.click.IOnCubeCallNativeListener
import com.hellobike.magiccube.v2.configs.MagicCube
import com.hellobike.magiccube.v2.configs.MagicInfo
import com.hellobike.magiccube.v2.preload.WKRequest
import kotlinx.android.synthetic.main.activity_preview.*
import java.lang.Exception

class PreviewActivity : AppCompatActivity(), IPreviewCallback {

    private val previewController = PreviewController(this)
    private val previewCache = PreviewCache(this)

    private var currentUrl: String? = null
    private var currentData: HashMap<String, Any?>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)


        if (previewCache.getIp().trim().isNotBlank()) {
            etIp.setText(previewCache.getIp(), TextView.BufferType.NORMAL)
        }

        if (previewCache.getPort().trim().isNotBlank()) {
            etPort.setText(previewCache.getPort(), TextView.BufferType.NORMAL)
        }

        btnStart.setOnClickListener {

            val ipStr: String = etIp.text?.trim()?.toString() ?: ""

            if (ipStr.isBlank()) {
                Toast.makeText(this, "请输入正确的ip地址", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            previewCache.saveIp(ipStr)

            val portStr = etPort.text?.trim()?.toString() ?: "7788"

            if (portStr.isNotBlank()) {
                previewCache.savePort(portStr)
            }

            val url = "http://$ipStr:$portStr"
            previewController.startPreview(url)

            WSLog.getInstance().init(url)
            WSLog.getInstance().setEnable(true)
        }

        btnStop.setOnClickListener {
            previewController.stopPreview()
        }

        val loader = WKLoaderCenter.createInsertLoader(
            this,
            object : IWKInsertLoaderListener.WKInsertLoaderListenerWrapper() {
                override fun onLoadSuccess(wkInsert: IWKInsert) {
                    wkInsert.process()
                    wkInsert.setOnInsertDialogListener(object : IWKInsertDialogListener {
                        override fun onShowing() {

                        }

                        override fun onDismiss(byUser: Boolean) {

                        }
                    })
                }

                override fun onLoadFailed(code: Int, message: String?) {

                }

                override fun onLoadEnd() {

                }
            })
        btnLoadDialog.setOnClickListener {
            val params = WKLoaderParam.Builder()
                .bindGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                .bindStyle(currentUrl)
                .bindData(currentData)
                .bindOnCubeCallNativeListener(object : IOnCubeCallNativeListener {
                    override fun onCallNative(params: Map<String, Any?>) {
                        // callnative
                    }
                })
                .build()
            loader.load(params)
        }
    }


    override fun onFailure(e: Exception) {
        e.printStackTrace()
        loge(e.toString())
    }

    private fun newEngine(styleJson: String, dataJson: String) {
        val data =
            JSONObject.parseObject(dataJson, object : TypeReference<HashMap<String, Any?>>() {})

        val url = previewController.getStyleUrl(styleJson)

        currentUrl = url
        currentData = data

        val request = WKRequest(url, data as HashMap<String, Any?>)

        MagicCube.preloadMetaData(request) {

            val magicInfo = MagicInfo.Builder()
                .bindMetaData(it.data)
                .bindOnCubeCallNativeListener(object :
                    IOnCubeCallNativeListener.IOnCubeCallNativeListenerWrapper() {
                    override fun onCallNative(params: Map<String, Any?>) {
                        super.onCallNative(params)
                        logd(">> js call native: $params")
                    }
                })
                .build()

            magicBoxLayout.loadByUrl(url, data, magicInfo, object : OnLoadStateListener {
                override fun onLoadSuccess() {

                }

                override fun onLoadFailed(code: Int, msg: String?) {
                    loge("加载失败! code: $code, msg: $msg")
                }

            })
        }

    }

    override fun onSuccess(styleJson: String, dataJson: String) {
        newEngine(styleJson, dataJson)
    }

    override fun finish() {
        previewController.stopPreview()
        WSLog.getInstance().setEnable(false)
        super.finish()
    }

    override fun onDestroy() {
        previewController.stopPreview()
        WSLog.getInstance().setEnable(false)
        super.onDestroy()
    }
}