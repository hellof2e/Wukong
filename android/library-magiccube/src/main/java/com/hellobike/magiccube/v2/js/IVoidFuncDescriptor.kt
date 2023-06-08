package com.hellobike.magiccube.v2.js

import com.hellobike.magiccube.v2.js.wrapper.WKJSObject

interface IVoidFuncDescriptor {

    fun invokeFailed(t: Throwable)

    fun invokeMethod(objHandle: WKJSObject)
}