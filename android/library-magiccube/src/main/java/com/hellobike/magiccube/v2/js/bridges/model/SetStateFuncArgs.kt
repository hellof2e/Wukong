package com.hellobike.magiccube.v2.js.bridges.model

import com.alibaba.fastjson.JSONObject

class SetStateFuncArgs {

    companion object {
        fun fromJson(json: String): SetStateFuncArgs {
            try {
                return JSONObject.parseObject(json, SetStateFuncArgs::class.java)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
            return SetStateFuncArgs().apply {
                this.datasetChanged = true
            }
        }

        fun fromJSONObject(jsonObject: org.json.JSONObject): SetStateFuncArgs {
            return fromJson(jsonObject.toString())
        }
    }

    var datasetChanged: Boolean = true
}