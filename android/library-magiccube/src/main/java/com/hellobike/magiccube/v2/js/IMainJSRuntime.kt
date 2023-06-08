package com.hellobike.magiccube.v2.js

import com.hellobike.magiccube.v2.js.bridges.wk.IWKVoidBridge
import com.hellobike.magiccube.v2.js.wrapper.WKJSObject
import com.quickjs.JSObject
import kotlin.jvm.Throws

interface IMainJSRuntime {

    @Throws(Throwable::class)
    fun syncLoadLogic(): WKJSObject

    /**
     * 为 main.wk 中注册 bridge
     */
    fun registerWkBridge(wkBridge: IWKVoidBridge)

    /**
     * 初始化
     */
    fun initLogic(logic: String?, data: HashMap<String, Any?>?)

    /**
     * 著出初始化参数
     */
    fun registerWKParams(params: Map<String, String>)

    /**
     * 异步执行函数
     */
    fun asyncExecVoidFunc(descriptor: IVoidFuncDescriptor)

    fun getWKVoidBridges(): Map<String, IWKVoidBridge>

    /**
     * 生命周期钩子函数
     */
    @Throws(Throwable::class)
    fun callBeforeCreate(): HashMap<String, Any?>

    fun callOnCreated()

    fun callOnUpdated()
    fun onAttachedToWindow()
    fun onDetachedFromWindow()
    fun onVisibilityChanged(isVisibility: Boolean)
    fun release()
}