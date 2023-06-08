package com.hellobike.magiccube.v2.js.wrapper

import com.quickjs.JSContext
import com.quickjs.JSValue
import org.json.JSONObject

interface IWKJSObject {

    fun getJSContext(): JSContext

    fun isUndefined(): Boolean

    fun isNull(): Boolean

    fun set(key: String, value: Int): IWKJSObject

    fun set(key: String, value: Double): IWKJSObject

    fun set(key: String, value: String): IWKJSObject

    fun set(key: String, value: JSValue): IWKJSObject

    fun set(key: String, value: WKJSObject): IWKJSObject

    fun setMap(key: String, map: Map<String, Any?>?): IWKJSObject

    fun containsFunction(key: String): Boolean

    fun getObject(key: String): IWKJSObject?

    fun getHashMap(key: String): HashMap<String, Any?>?

    fun executeVoidFunction(name: String, parameters: WKJSArray?)

    fun toJSONObject(): JSONObject

    fun getString(key: String): String?

    fun getArray(key: String): IWKJSArray?

    fun postEventQueue(runnable: Runnable)

    fun toHashMap(): HashMap<String, Any?>?
}