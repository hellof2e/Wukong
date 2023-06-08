package com.hellobike.magiccube.v2.js

import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.v2.js.bridges.wk.IWKVoidBridge
import com.hellobike.magiccube.v2.ext.loge
import com.hellobike.magiccube.v2.template.Grammar
import kotlin.jvm.Throws

class MagicCardJsEngine : IJsEngine {

    private val jsEngine: JSEngine by lazy { JSEngine.INSTANCE }

    private val mainJSRuntime: IMainJSRuntime by lazy { MainJSRuntime() }

    private var funcOnCreatedHasInvoke = false
    private var funcOnUpdatedHasInvoke = false

    override fun registerWkBridge(wkBridge: IWKVoidBridge) {
        mainJSRuntime.registerWkBridge(wkBridge)
    }

    override fun release() {
        tryCatch { mainJSRuntime.release() }
    }

    @Throws(Throwable::class)
    override fun callBeforeCreate(): HashMap<String, Any?> {
        return mainJSRuntime.callBeforeCreate()
    }

    override fun callOnCreated() {
        if (!funcOnCreatedHasInvoke) {
            funcOnCreatedHasInvoke = true
            tryCatch {
                mainJSRuntime.callOnCreated()
            }
        }
    }

    override fun callOnUpdated() {
        if (!funcOnUpdatedHasInvoke) {
            funcOnUpdatedHasInvoke = true
            tryCatch {
                mainJSRuntime.callOnUpdated()
            }
        }
    }

    override fun getWkVoidBridges(): Map<String, IWKVoidBridge> = mainJSRuntime.getWKVoidBridges()

    override fun registerWKParams(params: Map<String, String>) {
        mainJSRuntime.registerWKParams(params)
    }

    override fun asyncExecVoidFunc(descriptor: IVoidFuncDescriptor) {
        mainJSRuntime.asyncExecVoidFunc(descriptor)
    }

    override fun onAttachedToWindow() {
        tryCatch {
            mainJSRuntime.onAttachedToWindow()
        }
    }

    override fun onDetachedFromWindow() {
        tryCatch {
            mainJSRuntime.onDetachedFromWindow()
        }
    }

    override fun onVisibilityChanged(isVisibility: Boolean) {
        tryCatch {
            mainJSRuntime.onVisibilityChanged(isVisibility)
        }
    }

    @Throws(Throwable::class)
    override fun initLogic(logic: String, data: HashMap<String, Any?>, loadLogic: Boolean) {
        mainJSRuntime.initLogic(logic, data)
        if (loadLogic) { // 同步加载
            mainJSRuntime.syncLoadLogic()
        }
    }

    override fun executeScriptGlobal(script: String?): Any? {
        if (script.isNullOrBlank()) return null

        if (Grammar.isBoolExpression(script)) {
            return try {
                executeBoolScriptGlobal(Grammar.formatExpressionTemplate(script))
            } catch (t: Throwable) {
                loge("exception: $script \n $t")
                t.printStackTrace()
                false
            }
        }
        return try {
            jsEngine.executeScriptGlobal(Grammar.formatExpressionTemplate(script))
        } catch (t: Throwable) {
            loge("exception: $script \n $t")
            t.printStackTrace()
            null
        }
    }


    private fun executeBoolScriptGlobal(script: String?): Boolean {
        return try {
            jsEngine.executeBoolScriptGlobal(script)
        } catch (t: Throwable) {
            loge("exception: $script \n $t")
            t.printStackTrace()
            false
        }
    }
}