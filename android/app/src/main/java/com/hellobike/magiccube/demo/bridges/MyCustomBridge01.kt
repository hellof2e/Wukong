package com.hellobike.magiccube.demo.bridges

import com.hellobike.magiccube.v2.configs.ICardJSBridge
import com.hellobike.magiccube.v2.js.bridges.wk.IJSBridgeProto
import org.json.JSONObject

class MyCustomBridge01 : ICardJSBridge {

    override fun invoke(proto: IJSBridgeProto) {
        val jsonObj = proto.getArgs() ?: JSONObject()
        proto.callSuccess(jsonObj.apply { put("aa", "AA") })
        proto.callError(-1, "error", jsonObj.apply { put("bb", "BB") })
        proto.callCustomFunc("custom", jsonObj.apply { put("cc", "CC") })
    }

    override fun onAttachedToWindow() {
        // do nothing
    }

    override fun onDetachedFromWindow() {
        // do nothing
    }


    override fun onCardCreated() {
        // do nothing
    }

    override fun onCardUpdated() {
        // do nothing
    }

    override fun onVisibilityChanged(isVisibility: Boolean) {
        // do nothing
    }

    override fun release() {
        // do nothing
    }
}