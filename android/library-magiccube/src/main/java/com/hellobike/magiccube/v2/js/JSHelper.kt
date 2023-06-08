package com.hellobike.magiccube.v2.js

import com.quickjs.*

object JSHelper {

    fun jsObject2Map(jsObject: JSObject): HashMap<String, Any?>? {
        if (jsObject.isUndefined || jsObject == JSObject.NULL()) return null

        val keys = jsObject.keys ?: return null
        val length = keys.size

        val hashMap = HashMap<String, Any?>()

        for (index in 0 until length) {
            val key = keys[index]
            val value = jsObject.get(key)

            if (value is JSValue && value.isUndefined) continue
            if (value is JSFunction) continue

            if (value == null || value == JSValue.NULL()) {
                hashMap[key] = null
                continue
            }

            if (value is JSArray) {
                hashMap[key] = jsArray2List(value)
            } else if (value is JSObject) {
                hashMap[key] = jsObject2Map(value)
            } else if (value is Number || value is Boolean || value is String) {
                hashMap[key] = value
            }
        }

        return hashMap
    }

    fun jsArray2List(jsArray: JSArray): ArrayList<Any?>? {
        if (jsArray.isUndefined || jsArray == JSObject.NULL()) return null
        val length = jsArray.length()

        val arrayList = ArrayList<Any?>()
        for (index in 0 until length) {
            val value = jsArray.get(index)

            if (value is JSValue && value.isUndefined) continue
            if (value is JSFunction) continue

            if (value == null || value == JSValue.NULL()) {
                arrayList.add(null)
                continue
            }

             if (value is JSArray) {
                arrayList.add(jsArray2List(value))
            } else if (value is JSObject) {
                arrayList.add(jsObject2Map(value))
            } else if (value is Number || value is Boolean || value is String) {
                arrayList.add(value)
            }
        }
        return arrayList
    }

    fun map2JSObject(map: Map<String, Any?>?, context: JSContext): JSObject {
        if (map == null) return JSObject(context)

        val jsObject = JSObject(context)

        map.forEach {
            val key = it.key
            when (val value = it.value) {
                is Int -> {
                    jsObject.set(key, value)
                }
                is String -> {
                    jsObject.set(key, value)
                }
                is Boolean -> {
                    jsObject.set(key, value)
                }
                is Double -> {
                    jsObject.set(key, value)
                }
                is Number -> {
                    jsObject.set(key, value.toDouble())
                }
                is Map<*, *> -> {
                    jsObject.set(key, map2JSObject(value as Map<String, Any?>?, context))
                }
                is List<*> -> {
                    jsObject.set(key, list2JSArray(value, context))
                }
            }
        }
        return jsObject
    }

    fun list2JSArray(list: List<Any?>, context: JSContext): JSArray? {
        val jsArray = JSArray(context)
        list.forEach { value ->
            when (value) {
                is Int -> {
                    jsArray.push(value)
                }
                is String -> {
                    jsArray.push(value)
                }
                is Boolean -> {
                    jsArray.push(value)
                }
                is Double -> {
                    jsArray.push(value)
                }
                is Number -> {
                    jsArray.push(value.toDouble())
                }
                is Map<*, *> -> {
                    jsArray.push(map2JSObject(value as Map<String, Any?>?, context))
                }
                is List<*> -> {
                    jsArray.push(list2JSArray(value, context))
                }
            }
        }
        return jsArray
    }
}