package com.hellobike.magiccube.v2.js.wrapper

import com.quickjs.JSValue


interface IWKJSArray {

    fun get(index: Int): Any

    fun isUndefined(): Boolean

    fun isNull(): Boolean

    fun getJSObject(index: Int): IWKJSObject?

    fun push(value: WKJSObject): WKJSArray

    fun length(): Int

    fun push(value: WKJSArray): WKJSArray

    fun push(value: JSValue): WKJSArray

    fun push(value: Int): WKJSArray

    fun push(value: Double): WKJSArray

    fun push(value: Boolean): WKJSArray

    fun push(value: String): WKJSArray

    fun postEventQueue(runnable: Runnable)

    fun toArrayList(): ArrayList<Any?>?

}