package com.hellobike.magiccube.v2.js.bridges.wk

import com.hellobike.magiccube.v2.js.wrapper.WKJSObject

class MainJsObject(private val main: WKJSObject) {

    fun getJSContext() = main.context

    fun getJSData(): HashMap<String, Any?>? {
        return main.getHashMap("data")
    }
}