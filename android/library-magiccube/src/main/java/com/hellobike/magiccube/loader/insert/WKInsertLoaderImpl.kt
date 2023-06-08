package com.hellobike.magiccube.loader.insert

import android.content.Context
import android.os.Looper
import com.hellobike.magiccube.loader.WKLoaderParam
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.v2.OnLoadStateListener
import com.hellobike.magiccube.v2.configs.MagicInfo
import com.hellobike.magiccube.v2.ext.logd
import com.hellobike.magiccube.widget.MagicBoxLayout

class WKInsertLoaderImpl(
    private val context: Context,
    private val listener: IWKInsertLoaderListener? = null
) : IWKInsertLoader {

    override fun load(params: WKLoaderParam) {
        UIUtils.runOnUiThread {
            loadWithMain(params)
        }
    }

    private fun loadWithMain(params: WKLoaderParam) {
        val magicBoxLayout = MagicBoxLayout(context)
        val handler = MagicBoxEventHandler(params)
        val magicInfoBuilder = MagicInfo.Builder()
                .bindOnCubeClickListener(handler)
                .bindOnCubeCallNativeListener(handler)
                .bindOnCubeCountDownListener(handler)
        magicInfoBuilder.onCubeCustomerOperationHandler = handler
        val magicInfo = magicInfoBuilder.build()
        magicBoxLayout.loadByUrl(
                params.style,
                params.data,
                magicInfo,
                object : OnLoadStateListener {
                    private var hasLoadSuccess = false
                    override fun onLoadSuccess() {
                        if (!UIUtils.contextIsValid(context)) return
                        if (hasLoadSuccess) return
                        hasLoadSuccess = true

                        // 加载成功
                        val insert = WKInsertImpl(magicBoxLayout, params)
                        handler.bindWKInsert(insert)
                        listener?.onLoadSuccess(insert)
                        listener?.onLoadEnd()
                        logd("WKInsertLoader load success! [${params.style}]")
                    }

                    override fun onLoadFailed(code: Int, msg: String?) {
                        if (!UIUtils.contextIsValid(context)) return
                        if (hasLoadSuccess) return

                        listener?.onLoadFailed(code, msg)
                        listener?.onLoadEnd()
                        logd("WKInsertLoader load failed! [$code, $msg, ${params.style}]")
                    }
                }
        )
    }
}