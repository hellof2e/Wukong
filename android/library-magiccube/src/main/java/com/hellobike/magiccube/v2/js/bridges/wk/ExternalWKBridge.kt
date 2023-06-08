package com.hellobike.magiccube.v2.js.bridges.wk

import com.hellobike.magiccube.v2.configs.ICardJSBridge
import com.hellobike.magiccube.v2.js.wrapper.IWKJSArray
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject

internal class ExternalWKBridge(private val key: String, clz: Class<out ICardJSBridge>) :
    BaseCardWKBridge(), IWKVoidBridge {

    private val cardWKBridge: ICardJSBridge by lazy { clz.newInstance() }

    override fun getKey(): String = key

    override fun invoke(main: MainJsObject, receiver: IWKJSObject?, args: IWKJSArray?) {
        if (!isValidMagicCard()) return
        getMagicCard() ?: return
        getMagicConfig() ?: return
        getData() ?: return
        val jsObject = args?.getJSObject(0)
        val proto = JSBridgeProtoImpl(getKey(), jsObject, this)
        cardWKBridge.invoke(proto)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        cardWKBridge.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cardWKBridge.onDetachedFromWindow()
    }

    override fun onVisibilityChanged(isVisibility: Boolean) {
        super.onVisibilityChanged(isVisibility)
        cardWKBridge.onVisibilityChanged(isVisibility)
    }

    override fun jsLifecycleOnCreated() {
        super.jsLifecycleOnCreated()
        cardWKBridge.onCardCreated()
    }

    override fun jsLifecycleOnUpdated() {
        super.jsLifecycleOnUpdated()
        cardWKBridge.onCardUpdated()
    }

    override fun release() {
        super.release()
        cardWKBridge.release()
    }

}