package com.hellobike.magiccube.v2.js.bridges.wk

import android.app.Activity
import android.view.Gravity
import com.hellobike.magiccube.loader.WKLoaderCenter
import com.hellobike.magiccube.loader.WKLoaderParam
import com.hellobike.magiccube.loader.insert.IWKInsert
import com.hellobike.magiccube.loader.insert.IWKInsertDialogListener
import com.hellobike.magiccube.loader.insert.IWKInsertLoaderListener
import com.hellobike.magiccube.v2.click.IOnCubeCallNativeListener
import com.hellobike.magiccube.v2.js.JSHelper
import com.hellobike.magiccube.v2.js.wrapper.IWKJSArray
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject
import com.hellobike.magiccube.v2.js.wrapper.WKJSArray
import com.hellobike.magiccube.v2.js.wrapper.WKJSObject

internal class WKShowWukongDialogBridge : BaseCardWKBridge(), IWKVoidBridge {

    override fun getKey(): String = "showWKDialog"

    override fun invoke(main: MainJsObject, receiver: IWKJSObject?, args: IWKJSArray?) {
        if (!isValidMagicCard()) return
        val context = getContext() as? Activity ?: return
        val magicConfig = getMagicConfig() ?: return
        val jsObject = args?.getJSObject(0) ?: return
        if (jsObject.isUndefined() || jsObject.isNull()) return

        val params = jsObject.getHashMap("params") ?: HashMap()

        val style = params["style"] as? String ?: return
        val data = params["data"] as? HashMap<String, Any?> ?: HashMap()

        val gravity = when (params["gravity"].toString()) {
            "top" -> Gravity.TOP or Gravity.CENTER_HORIZONTAL
            "center" -> Gravity.CENTER or Gravity.CENTER_HORIZONTAL
            "bottom" -> Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            else -> Gravity.CENTER or Gravity.CENTER_HORIZONTAL
        }
        val canceledOnTouchOutside =
            params["canceledOnTouchOutside"]?.toString()?.toBoolean() ?: true

        val loader = WKLoaderCenter.createInsertLoader(context,
            object : IWKInsertLoaderListener.WKInsertLoaderListenerWrapper() {
                override fun onLoadSuccess(wkInsert: IWKInsert) {
                    super.onLoadSuccess(wkInsert)
                    wkInsert.setOnInsertDialogListener(object :
                        IWKInsertDialogListener.WKInsertDialogListenerWrapper() {
                        override fun onDismiss(byUser: Boolean) {
                            super.onDismiss(byUser)
                            if (!isValidMagicCard()) return
                            callMethod(jsObject, "onShow", null)
                        }

                        override fun onShowing() {
                            super.onShowing()
                            if (!isValidMagicCard()) return
                            callMethod(jsObject, "onDismiss", null)
                        }
                    })
                    wkInsert.process()
                }

                override fun onLoadFailed(code: Int, message: String?) {
                    super.onLoadFailed(code, message)
                    // do nothing
                }
            })

        val loaderParams = WKLoaderParam.Builder()
            .bindCanceledOnTouchOutside(canceledOnTouchOutside)
            .bindGravity(gravity)
            .bindData(data)
            .bindStyle(style)
            .bindOnCubeCallNativeListener(object :
                IOnCubeCallNativeListener.IOnCubeCallNativeListenerWrapper() {
                override fun onCallNative(params: Map<String, Any?>) {
                    super.onCallNative(params)
                    if (!isValidMagicCard()) return

                    callMethod(
                        jsObject,
                        "onEvent",
                        WKJSArray(main.getJSContext()).push(
                            WKJSObject(
                                JSHelper.map2JSObject(params, main.getJSContext())
                            )
                        )
                    )
                }
            })
            .build()
        loader.load(loaderParams)
    }
}