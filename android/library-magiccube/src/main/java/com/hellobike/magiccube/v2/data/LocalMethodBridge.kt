package com.hellobike.magiccube.v2.data

import android.os.Build
import com.hellobike.magiccube.v2.configs.Constants
import com.hellobike.magiccube.v2.configs.MagicCube
import com.hellobike.magiccube.v2.ext.logd
import org.json.JSONArray
import org.json.JSONObject

object LocalMethodBridge {

    fun parseLocalMethod(methodName: String): SafeMap {
        val result: Any? = when (methodName) {
            "platform" -> { "Android" }
            "deviceModel" -> { // 设备名称
                Build.MODEL
            }
            else -> {
                null
            }
        }
        logd("LocalMethodBridge >> Local.$methodName = $result")
        return if (result != null) {
            SafeMap(result)
        } else {
            Constants.NULL_VALUE
        }
    }

    /**
     * 解析 size() 方法
     */
    fun parseFuncSize(data: Any?): Int = when (data) {
        is List<*> -> data.size
        is Map<*, *> -> data.size
        is JSONObject -> data.length()
        is JSONArray -> data.length()
        is com.alibaba.fastjson.JSONObject -> data.size
        is com.alibaba.fastjson.JSONArray -> data.size
        is String -> data.length
        else -> 0
    }

    /**
     * 解析 isNullOrEmpty() 方法
     */
    fun parseFuncIsNullOrEmpty(data: Any?): Boolean = when (data) {
        data == null -> true
        is List<*> -> data.isEmpty()
        is Map<*, *> -> data.isEmpty()
        is JSONObject -> data.length() <= 0
        is JSONArray -> data.length() <= 0
        is com.alibaba.fastjson.JSONObject -> data.size <= 0
        is com.alibaba.fastjson.JSONArray -> data.size <= 0
        is String -> data.isEmpty()
        else -> false
    }

}