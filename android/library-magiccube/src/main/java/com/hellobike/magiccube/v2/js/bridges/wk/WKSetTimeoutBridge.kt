package com.hellobike.magiccube.v2.js.bridges.wk

import android.app.Activity
import android.os.Handler
import android.os.Looper
import com.hellobike.magiccube.v2.js.wrapper.IWKJSArray
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject
import com.quickjs.JSFunction
import com.quickjs.JSObject

internal class WKSetTimeoutBridge : BaseCardWKBridge(), IWKVoidBridge {
    override fun getKey(): String = "setTimeout"

    override fun invoke(main: MainJsObject, receiver: IWKJSObject?, args: IWKJSArray?) {
        if (!isValidMagicCard()) return
        val context = getContext() as? Activity ?: return
        val magicConfig = getMagicConfig() ?: return
        val params = args?.getJSObject(0) ?: return
        if (params.isUndefined() || params.isNull()) return


        if (!params.containsFunction("callback")) return

        val delayMillis = params.getString("delayMillis")?.toLongOrNull() ?: return

        getMagicCard()?.getCardContext()?.postDelayed({
            if (!isValidMagicCard()) {
                clearTimeoutTask()
                return@postDelayed
            }
            callMethod(params, "callback", null)

        }, getKey().hashCode(), delayMillis)
    }

    override fun jsLifecycleOnCreated() {
        super.jsLifecycleOnCreated()
        clearTimeoutTask()
    }

    override fun jsLifecycleOnUpdated() {
        super.jsLifecycleOnUpdated()
        clearTimeoutTask()
    }

    private fun clearTimeoutTask() {
        getMagicCard()?.getCardContext()?.removeMessages(getKey().hashCode())
    }
}