package com.hellobike.magiccube.v2.js.wrapper

import com.hellobike.magiccube.v2.js.JSHelper
import com.quickjs.JSArray
import com.quickjs.JSContext
import com.quickjs.JSObject
import com.quickjs.JSValue
import org.json.JSONArray


class WKJSArray(
    jsContext: JSContext,
    jsonArray: JSONArray? = null,
    arr: JSArray? = null
) : IWKJSArray {

    constructor(jsArray: JSArray) : this(jsArray.context, null, jsArray)

    internal val jsArray: JSArray = arr ?: if (jsonArray == null) {
        JSArray(jsContext)
    } else {
        JSArray(jsContext, jsonArray)
    }

    internal val context = jsContext

    override fun isNull(): Boolean {
        return jsArray == JSObject.NULL()
    }

    override fun isUndefined(): Boolean {
        return jsArray.isUndefined
    }
    override fun get(index: Int): Any {
        return jsArray.get(index)
    }

    override fun getJSObject(index: Int): IWKJSObject? {
        val jsObject = get(index) as? JSObject ?: return null
        return WKJSObject(context, null, jsObject)
    }

    override fun push(value: WKJSObject): WKJSArray {
        jsArray.push(value.jsObject)
        return this
    }

    override fun push(value: WKJSArray): WKJSArray {
        jsArray.push(value.jsArray)
        return this
    }

    override fun length(): Int = jsArray.length()

    override fun push(value: JSValue): WKJSArray {
        jsArray.push(value)
        return this
    }

    override fun push(value: Int): WKJSArray {
        jsArray.push(value)
        return this
    }

    override fun push(value: Double): WKJSArray {
        jsArray.push(value)
        return this
    }

    override fun push(value: Boolean): WKJSArray {
        jsArray.push(value)
        return this
    }

    override fun push(value: String): WKJSArray {
        jsArray.push(value)
        return this
    }

    override fun postEventQueue(runnable: Runnable) {
        jsArray.postEventQueue(runnable)
    }

    override fun toArrayList(): ArrayList<Any?>? {
        return JSHelper.jsArray2List(jsArray)
    }
}