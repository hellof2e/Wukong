package com.hellobike.magiccube.v2.js.bridges.global

import android.content.Context
import android.webkit.JavascriptInterface
import com.hellobike.magiccube.StyleManager
import com.hellobike.magiccube.utils.Toasts.toast
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.v2.configs.IUserManager
import com.hellobike.magiccube.v2.configs.MagicCube
import com.hellobike.magiccube.v2.js.JSHelper
import com.hellobike.magiccube.v2.js.MainJSEngine
import com.quickjs.JSFunction
import com.quickjs.JSObject

class WukongBridge {

    @JavascriptInterface
    fun preloadDSLStyle(url: String?) {
        if (url.isNullOrBlank()) return
        MagicCube.preloads(arrayListOf(url))
    }

    @JavascriptInterface
    fun cacheItem(file: String?, key: String?, value: String?) {
        val context = StyleManager.context ?: return
        if (key.isNullOrBlank()) return
        if (file.isNullOrBlank()) return
        context.getSharedPreferences(file, Context.MODE_PRIVATE).edit()
            .putString(key, value ?: "").apply()
    }

    @JavascriptInterface
    fun readItem(file: String?, key: String?): String? {
        val context = StyleManager.context ?: return null
        if (key.isNullOrBlank()) return null
        if (file.isNullOrBlank()) return null
        return context.getSharedPreferences(file, Context.MODE_PRIVATE).getString(key, null)
    }

    @JavascriptInterface
    fun toast(content: String?) {
        val context = StyleManager.context
        if (context == null || content.isNullOrBlank()) return
        UIUtils.runOnUiThread {
            context.toast(content)
        }
    }

    @JavascriptInterface
    fun getUserInfo(): JSObject {
        return MainJSEngine.INSTANCE.createJSObjectByMap(MagicCube.starter.userManager?.getUserInfo())
    }

    @JavascriptInterface
    fun navigator(url: String?) {
        if (url.isNullOrBlank()) return
        MagicCube.starter.router?.navigator(url)
    }

    @JavascriptInterface
    fun hasLogin(): Boolean {
        return MagicCube.starter.userManager?.hasLogin() == true
    }

    @JavascriptInterface
    fun login(params: JSObject?) {
        if (params == null) return
        UIUtils.runOnUiThread {
            MagicCube.starter.userManager?.gotoLogin(object : IUserManager.ILoginHandler {
                override fun handleLoginSuccess() {
                    tryCatch {
                        params.postEventQueue {
                            if (params.get("success") is JSFunction) {
                                params.executeVoidFunction("success", null)
                            }
                        }
                    }
                }

                override fun handleLoginFailed() {
                    tryCatch {
                        params.postEventQueue {
                            if (params.get("error") is JSFunction) {
                                params.executeVoidFunction("error", null)
                            }
                        }
                    }
                }
            })
        }
    }

    @JavascriptInterface
    fun getLocation(): JSObject {
        val ext = MagicCube.starter.locationManager?.getLocationInfo()
        return MainJSEngine.INSTANCE.createJSObjectByMap(ext)
    }

    @JavascriptInterface
    fun getDeviceInfo(): JSObject {
        val device = MagicCube.starter.deviceInfo?.getDeviceInfo()
        val jsObject = MainJSEngine.INSTANCE.createJSObjectByMap(device)
        tryCatch {
            jsObject.set("platform", "Android")
        }
        return jsObject
    }

    @JavascriptInterface
    fun trackClick(params: JSObject?) {
        tryCatch {
            val p = if (params == null) null else JSHelper.jsObject2Map(params)
            MagicCube.starter.reportEvent?.trackClick(p)
        }
    }

    @JavascriptInterface
    fun trackCustom(params: JSObject?) {
        tryCatch {
            val p = if (params == null) null else JSHelper.jsObject2Map(params)
            MagicCube.starter.reportEvent?.trackCustom(p)
        }
    }

    @JavascriptInterface
    fun trackExpose(params: JSObject?) {
        tryCatch {
            val p = if (params == null) null else JSHelper.jsObject2Map(params)
            MagicCube.starter.reportEvent?.trackExpose(p)
        }
    }
}