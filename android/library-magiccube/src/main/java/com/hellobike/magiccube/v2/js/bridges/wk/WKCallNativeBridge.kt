package com.hellobike.magiccube.v2.js.bridges.wk

import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.v2.js.wrapper.IWKJSArray
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject
import org.json.JSONObject

internal class WKCallNativeBridge: BaseCardWKBridge(), IWKVoidBridge {

    override fun getKey(): String = "callNative"

    override fun invoke(main: MainJsObject, receiver: IWKJSObject?, args: IWKJSArray?) {
        if (!isValidMagicCard()) return
        val magicConfig = getMagicConfig() ?: return
        val params = args?.getJSObject(0) ?: return

        val map = params.toHashMap() ?: HashMap()

        getMagicCard()?.getCardContext()?.runOnUiThread {
            tryCatch {
                magicConfig.magicParams?.onCubeCallNativeListener?.onCallNative(map)
            }
        }
    }

}