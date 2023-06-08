package com.hellobike.magiccube.v2.js

import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.v2.ext.logd
import com.hellobike.magiccube.v2.js.bridges.wk.IWKVoidBridge
import com.hellobike.magiccube.v2.js.bridges.wk.MainJsObject
import com.hellobike.magiccube.v2.js.wrapper.*
import java.lang.RuntimeException
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.HashMap
import kotlin.jvm.Throws

class MainJSRuntime : IMainJSRuntime {

    private val jsEngine: MainJSEngine = MainJSEngine.INSTANCE

    private var main: WKJSObject? = null

    private var logic: String? = null

    private var data: HashMap<String, Any?>? = null

    // 无返回值的 wk bridge
    private val wkVoidBridgeMap: ConcurrentHashMap<String, IWKVoidBridge> = ConcurrentHashMap()

    // 注入的初始化参数
    private val injectedWKParams by lazy { HashMap<String, String>() }

    override fun getWKVoidBridges(): Map<String, IWKVoidBridge> = wkVoidBridgeMap

    override fun registerWkBridge(wkBridge: IWKVoidBridge) {
        wkVoidBridgeMap[wkBridge.getKey()] = wkBridge
    }

    override fun initLogic(logic: String?, data: HashMap<String, Any?>?) {
        this.logic = logic
        this.data = data
    }

    override fun registerWKParams(params: Map<String, String>) {
        injectedWKParams.putAll(params)
        // 如果 main 已经存在则直接更新wk中的数据
        tryCatch {
            val wk = this.main?.getObject("wk")
            if (wk != null) {
                injectedWKParams.forEach { wk.set(it.key, it.value) }
            }
        }
    }

    // 异步执行js函数
    override fun asyncExecVoidFunc(descriptor: IVoidFuncDescriptor) {
        val main = this.main
        if (main != null) {
            MainJSEngine.postJSQueue {
                try {
                    descriptor.invokeMethod(main)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    descriptor.invokeFailed(t)
                }
            }
        } else {
            asyncLoadLogic({
                descriptor.invokeMethod(it)
            }, {
                descriptor.invokeFailed(it)
            })
        }
    }

    @Throws(Throwable::class)
    override fun syncLoadLogic(): WKJSObject = loadLogic()

    private fun asyncLoadLogic(success: ((WKJSObject) -> Unit)?, failed: ((Throwable) -> Unit)?) {
        MainJSEngine.postJSQueue {
            try {
                val main = syncLoadLogic()
                success?.invoke(main)
            } catch (t: Throwable) {
                t.printStackTrace()
                failed?.invoke(t)
            }
        }
    }


    @Throws(Throwable::class)
    override fun callBeforeCreate(): HashMap<String, Any?> {
        val main = this.main ?: throw RuntimeException("beforeCreate 无法执行！main 对象还没有初始化")
        if (main.containsFunction("beforeCreate")) {
            main.executeVoidFunction("beforeCreate", null)
            return main.getHashMap("data") ?: HashMap()
        }
        return HashMap()
    }

    override fun callOnCreated() {
        val main = this.main ?: return
        main.postEventQueue {
//            logd("callOnCreated : ${main.containsFunction("onCreated")}")
            if (main.containsFunction("onCreated")) {
                wkVoidBridgeMap.forEach { entry ->
                    entry.value.jsLifecycleOnCreated()
                }
                main.executeVoidFunction("onCreated", null)
            }
        }
    }

    override fun callOnUpdated() {
        val main = this.main ?: return
        main.postEventQueue {
//            logd("callOnUpdated : ${main.containsFunction("onUpdated")}")
            if (main.containsFunction("onUpdated")) {
                wkVoidBridgeMap.forEach { entry ->
                    entry.value.jsLifecycleOnUpdated()
                }
                main.executeVoidFunction("onUpdated", null)
            }
        }
    }

    override fun onAttachedToWindow() {
        wkVoidBridgeMap.forEach { entry ->
            entry.value.onAttachedToWindow()
        }
    }

    override fun onDetachedFromWindow() {
        wkVoidBridgeMap.forEach { entry ->
            entry.value.onDetachedFromWindow()
        }
    }

    override fun release() {
        wkVoidBridgeMap.forEach { entry ->
            entry.value.release()
        }
    }

    override fun onVisibilityChanged(isVisibility: Boolean) {
        wkVoidBridgeMap.forEach { entry ->
            entry.value.onVisibilityChanged(isVisibility)
        }
    }

    @Throws(Throwable::class)
    private fun loadLogic(): WKJSObject {
        val logic = this.logic

        var main = this.main

        try {
            if (main == null && !logic.isNullOrBlank()) {
                main = WKJSObject(jsEngine.executeObjectScript(logic))
            }
            if (main != null) {
                initMain(main, data)
            }
            this.main = main
        } catch (t: Throwable) {
            t.printStackTrace()
            this.main = null
            throw t
        }

        if (main == null) {
            throw RuntimeException("load logic 失败！main == null")
        }
        return main
    }

    private fun initMain(main: WKJSObject, data: HashMap<String, Any?>?) {
        val wk = WKJSObject(main.context)
        wkVoidBridgeMap.forEach {
            val key = it.key
            val value = it.value
            wk.setJSVoidFunction(key) { receiver: IWKJSObject?, args: IWKJSArray? ->
                tryCatch {
                    logd("[${Thread.currentThread().name}] $key invoke...")
                    value.invoke(MainJsObject(main), receiver, args)
                }
            }

        }
        injectedWKParams.forEach { wk.set(it.key, it.value) }
        main.set("wk", wk)
        main.setMap("data", data)
    }
}