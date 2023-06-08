package com.hellobike.magiccube.v2.template.descriptor

import com.hellobike.magiccube.v2.ext.logd
import com.hellobike.magiccube.v2.js.IVoidFuncDescriptor
import com.hellobike.magiccube.v2.js.wrapper.WKJSObject

class JSFuncDescriptor : BaseFunctionDescriptor(), IVoidFuncDescriptor {

    override fun invokeFailed(t: Throwable) {
        reportInvokeFailed(t)
    }

    override fun invokeMethod(objHandle: WKJSObject) {
        val start = System.currentTimeMillis()
        if (objHandle.containsFunction(funcName)) {
            val params = convertArgs()
            objHandle.executeVoidFunction(funcName, params)
        }
        logd("Thread[${Thread.currentThread().name}] invokeMethod $funcName() >>  duration: ${System.currentTimeMillis() - start}")
    }
}