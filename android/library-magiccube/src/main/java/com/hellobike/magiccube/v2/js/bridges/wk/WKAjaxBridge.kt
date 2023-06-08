package com.hellobike.magiccube.v2.js.bridges.wk

import com.hellobike.magiccube.v2.configs.IAjaxResponseHandler
import com.hellobike.magiccube.v2.configs.MagicCube
import com.hellobike.magiccube.v2.js.JSHelper
import com.hellobike.magiccube.v2.js.wrapper.IWKJSArray
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject
import com.hellobike.magiccube.v2.js.wrapper.WKJSArray
import com.hellobike.magiccube.v2.js.wrapper.WKJSObject
import org.json.JSONObject

internal class WKAjaxBridge : BaseCardWKBridge(), IWKVoidBridge {

    override fun getKey(): String = "ajax"

    override fun invoke(main: MainJsObject, receiver: IWKJSObject?, args: IWKJSArray?) {

        val magicConfig = getMagicConfig() ?: return

        if (!isValidMagicCard()) return

        val jsObject = args?.getJSObject(0) ?: return

        if (jsObject.isUndefined() || jsObject.isNull()) return

        val url = jsObject.getString("url") ?: return

        val method = jsObject.getString("type") ?: "POST" // 请求方式

        val params = jsObject.getHashMap("data") ?: return

        when (method) {
            "POST" -> {
                MagicCube.starter.net?.ajaxPost(url, params, object : IAjaxResponseHandler {
                    override fun handleResponse(response: JSONObject) {
                        callMethod(
                            jsObject, "success",
                            WKJSArray(main.getJSContext()).push(
                                WKJSObject(main.getJSContext(), response)
                            )
                        )
                    }

                    override fun handleError(code: Int, message: String) {
                        callMethod(jsObject, "error", null)
                    }
                })

            }
            "GET" -> {
                MagicCube.starter.net?.ajaxGet(url, params, object : IAjaxResponseHandler {
                    override fun handleResponse(response: JSONObject) {
                        callMethod(
                            jsObject, "success",
                            WKJSArray(main.getJSContext()).push(
                                WKJSObject(main.getJSContext(), response)
                            )
                        )
                    }

                    override fun handleError(code: Int, message: String) {
                        callMethod(jsObject, "error", null)
                    }
                })
            }
            else -> {
                MagicCube.starter.net?.ajax(url, method, params, object : IAjaxResponseHandler {
                    override fun handleResponse(response: JSONObject) {
                        callMethod(
                            jsObject, "success",
                            WKJSArray(main.getJSContext()).push(
                                WKJSObject(main.getJSContext(), response)
                            )
                        )
                    }

                    override fun handleError(code: Int, message: String) {
                        callMethod(jsObject, "error", null)
                    }
                })
            }
        }

//
//
//        coroutineSupport.launch(Dispatchers.IO) {
//
//            try {
//                val action = params.remove("action") as? String
//                val module = params.remove("module") as? String
//
//                if (action.isNullOrBlank() || module.isNullOrBlank()) {
//                    loge("action 和 module 不能为空！action: $action module: $module")
//                    return@launch
//                }
//
//                if (method == "POST") {
//                    val response = MagicNetClient.suspendRequestV2(module, action, params)
//
//                    val jsResponseMap = HashMap<String, Any?>()
//                    jsResponseMap["code"] = response.code
//                    jsResponseMap["msg"] = response.msg
//                    jsResponseMap["data"] = response.data
//
//                    jsObject.postEventQueue {
//                        if (jsObject.containsFunction("success")) {
//
//                            jsObject.executeVoidFunction(
//                                "success",
//                                WKJSArray(main.getWKJSObject().context)
//                                    .push(JSHelper.map2JSObject(jsResponseMap, main.getJSContext()))
//                            )
//                        }
//                    }
//                }
//            } catch (t: Throwable) {
//                t.printStackTrace()
//                tryCatch {
//                    jsObject.postEventQueue {
//                        if (jsObject.containsFunction("error")) {
//                            jsObject.executeVoidFunction("error", null)
//                        }
//                    }
//                }
//            }
//        }
    }
}