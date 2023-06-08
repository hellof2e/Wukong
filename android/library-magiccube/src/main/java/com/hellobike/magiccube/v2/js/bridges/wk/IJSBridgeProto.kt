package com.hellobike.magiccube.v2.js.bridges.wk

import android.content.Context
import org.json.JSONObject

interface IJSBridgeProto {

    fun getContext(): Context?

    /**
     * 切换主线程
     */
    fun runOnUiThread(runnable: Runnable)

    /**
     * 获取 js 参数
     */
    fun getArgs(): JSONObject?

    /**
     * 回调 js success 方法。
     * @param data { 'code': 0, 'msg': 'success', 'data': data }
     */
    fun callSuccess(data: JSONObject?)

    /**
     * 回调 js error 方法。
     * @param data { 'code': code, 'msg': msg, 'data': data }
     */
    fun callError(code: Int, msg: String, data: JSONObject?)

    /**
     * 回调 js 自定义 方法。
     * @param methodName 方法名称
     * @param response 回调的参数
     */
    fun callCustomFunc(methodName: String, response: JSONObject?)
}