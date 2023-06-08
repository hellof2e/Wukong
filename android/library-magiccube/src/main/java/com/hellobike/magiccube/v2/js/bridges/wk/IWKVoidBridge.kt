package com.hellobike.magiccube.v2.js.bridges.wk

import com.hellobike.magiccube.v2.js.wrapper.IWKJSArray
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject

interface IWKVoidBridge {

    fun getKey(): String

    fun invoke(main: MainJsObject, receiver: IWKJSObject?, args: IWKJSArray?)

    fun onAttachedToWindow() {

    }

    fun onDetachedFromWindow() {

    }

    fun onVisibilityChanged(isVisibility: Boolean) {

    }

    fun release()

    fun jsLifecycleOnCreated() {

    }

    fun jsLifecycleOnUpdated() {

    }


}