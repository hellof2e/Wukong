package com.hellobike.magiccube.v2.js.bridges.wk

import android.content.Context
import com.hellobike.magiccube.v2.ext.loge
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject
import com.hellobike.magiccube.v2.js.wrapper.WKJSArray
import com.hellobike.magiccube.v2.js.wrapper.WKJSObject
import com.hellobike.magiccube.v2.reports.Codes
import org.json.JSONObject
import java.lang.ref.WeakReference

internal class JSBridgeProtoImpl(
    private val funcName: String,
    private val funcArg: IWKJSObject?,
    baseCardWKBridge: BaseCardWKBridge
) : IJSBridgeProto {

    private val cardBridge: WeakReference<BaseCardWKBridge> = WeakReference(baseCardWKBridge)

    private val jsonObjArg: JSONObject? by lazy { funcArg?.toJSONObject() }

    override fun getContext(): Context? = cardBridge.get()?.getContext()

    override fun runOnUiThread(runnable: Runnable) {
        cardBridge.get()?.runOnUIThread(runnable)
    }

    override fun getArgs(): JSONObject? = jsonObjArg

    override fun callSuccess(data: JSONObject?) {
        val jsObj = funcArg
        if (jsObj == null) {
            loge("$funcName() 没有接收 success 回调的有效参数!")
            return
        }
        val jsContext = jsObj.getJSContext()
        val response = WKJSObject(jsContext)
        response.set("code", Codes.SUCCESS)
        response.set("msg", "success")
        if (data != null) response.set("data", WKJSObject(jsContext, data))
        val args = WKJSArray(jsContext).push(response)
        cardBridge.get()?.callMethod(jsObj, "success", args)
    }

    override fun callError(code: Int, msg: String, data: JSONObject?) {
        val jsObj = funcArg
        if (jsObj == null) {
            loge("$funcName() 没有接收 error 回调的有效参数!")
            return
        }
        val jsContext = jsObj.getJSContext()
        val response = WKJSObject(jsContext)
        response.set("code", code)
        response.set("msg", msg)
        if (data != null) response.set("data", WKJSObject(jsContext, data))
        val args = WKJSArray(jsContext).push(response)
        cardBridge.get()?.callMethod(jsObj, "error", args)
    }

    override fun callCustomFunc(methodName: String, response: JSONObject?) {
        val jsObj = funcArg
        if (jsObj == null) {
            loge("$funcName() 没有接收 $methodName 回调的有效参数!")
            return
        }
        if (response == null) {
            cardBridge.get()?.callMethod(jsObj, methodName, null)
        } else {
            val jsContext = jsObj.getJSContext()
            val args = WKJSArray(jsContext).push(WKJSObject(jsContext, response))
            cardBridge.get()?.callMethod(jsObj, methodName, args)
        }
    }
}