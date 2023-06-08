package com.hellobike.magiccube.v2.js

import com.hellobike.magiccube.v2.js.bridges.wk.IWKVoidBridge
import kotlin.jvm.Throws

interface IJsEngine {

    fun release()

    @Throws(Throwable::class)
    fun callBeforeCreate(): HashMap<String, Any?>

    fun callOnCreated()

    fun callOnUpdated()

    // 获取所有的 bridge
    fun getWkVoidBridges(): Map<String, IWKVoidBridge>

    // 注册 bridge
    fun registerWkBridge(wkBridge: IWKVoidBridge)

    // 异步执行 js function
    fun asyncExecVoidFunc(descriptor: IVoidFuncDescriptor)

    fun registerWKParams(params: Map<String, String>)

    // 初始化
    @Throws(Throwable::class)
    fun initLogic(logic: String, data: HashMap<String, Any?>, loadLogic: Boolean)

    // 执行 js 表达式
    fun executeScriptGlobal(script: String?): Any?

    fun onAttachedToWindow()

    fun onDetachedFromWindow()

    fun onVisibilityChanged(isVisibility: Boolean)
}