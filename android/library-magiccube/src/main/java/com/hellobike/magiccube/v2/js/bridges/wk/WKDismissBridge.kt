package com.hellobike.magiccube.v2.js.bridges.wk

import android.app.Activity
import com.hellobike.magiccube.v2.js.wrapper.IWKJSArray
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject

internal class WKDismissBridge : BaseCardWKBridge(), IWKVoidBridge {

    override fun getKey(): String = "dismiss"

    override fun invoke(main: MainJsObject, receiver: IWKJSObject?, args: IWKJSArray?) {
        if (!isValidMagicCard()) return
        val context = getContext() as? Activity ?: return
        val magicConfig = getMagicConfig() ?: return
        magicConfig.magicParams?.onCubeCustomerOperationHandler?.onDismiss()
    }
}