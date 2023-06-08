package com.hellobike.magiccube.v2.js.wrapper

import com.hellobike.magiccube.v2.js.JSHelper
import com.quickjs.*
import org.json.JSONObject

open class WKJSObject(
    private val jsContext: JSContext,
    jsonObj: JSONObject? = null,
    obj: JSObject? = null
) : IWKJSObject {

    constructor(jsObject: JSObject) : this(jsObject.context, null, jsObject)

    internal val jsObject: JSObject = obj ?: if (jsonObj == null) {
        JSObject(jsContext)
    } else {
        JSObject(jsContext, jsonObj)
    }

    internal val context = jsContext

    override fun getJSContext(): JSContext = jsContext


    override fun isUndefined(): Boolean {
        return jsObject.isUndefined
    }


    override fun isNull(): Boolean {
        return jsObject == JSObject.NULL()
    }

    override fun set(key: String, value: Int): IWKJSObject {
        jsObject.set(key, value)
        return this
    }

    override fun set(key: String, value: Double): IWKJSObject {
        jsObject.set(key, value)
        return this
    }

    override fun set(key: String, value: String): IWKJSObject {
        jsObject.set(key, value)
        return this
    }

    override fun set(key: String, value: JSValue): IWKJSObject {
        jsObject.set(key, value)
        return this
    }

    override fun set(key: String, value: WKJSObject): IWKJSObject {
        jsObject.set(key, value.jsObject)
        return this
    }


    override fun setMap(key: String, map: Map<String, Any?>?): IWKJSObject {
        jsObject.set(key, JSHelper.map2JSObject(map, context))
        return this
    }


    override fun containsFunction(key: String): Boolean {
        return jsObject.get(key) is JSFunction
    }

    fun setJSVoidFunction(
        key: String,
        javaVoidCallback: ((receiver: IWKJSObject?, args: IWKJSArray?) -> Unit)
    ) {
        jsObject.set(key, JSFunction(jsContext) { receiver, args ->
            javaVoidCallback.invoke(WKJSObject(receiver), WKJSArray(args))
        })
    }

    override fun getObject(key: String): IWKJSObject? {
        val obj = jsObject.getObject(key) ?: return null
        return WKJSObject(obj)
    }

    override fun getHashMap(key: String): HashMap<String, Any?>? {
        val obj = jsObject.getObject(key) ?: return null
        return JSHelper.jsObject2Map(obj)
    }

    override fun executeVoidFunction(name: String, parameters: WKJSArray?) {
        if (containsFunction(name)) {
            jsObject.executeVoidFunction(name, parameters?.jsArray)
        }
    }

    override fun toJSONObject(): JSONObject {
        if (jsObject.isUndefined || jsObject == JSObject.NULL()) {
            return JSONObject()
        }
        return jsObject.toJSONObject()
    }

    override fun getString(key: String): String? {
        return jsObject.getString(key)
    }

    override fun getArray(key: String): IWKJSArray? {
        val jsArray = jsObject.get(key) as? JSArray ?: return null
        return WKJSArray(context, null, jsArray)
    }

    override fun postEventQueue(runnable: Runnable) {
        jsObject.postEventQueue(runnable)
    }

    override fun toHashMap(): HashMap<String, Any?>? {
        return JSHelper.jsObject2Map(jsObject)
    }
}