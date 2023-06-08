package com.hellobike.magiccube.v2.js

import com.hellobike.magiccube.utils.EncryptUtils
import com.hellobike.magiccube.v2.configs.MagicCube
import com.hellobike.magiccube.v2.js.bridges.global.WukongBridge
import com.hellobike.magiccube.v2.js.bridges.global.WukongConsolePlugin
import com.quickjs.JSContext
import com.quickjs.JSObject
import com.quickjs.QuickJS
import org.json.JSONObject


internal class MainJSEngine {

    private val jsRuntime: QuickJS by lazy { QuickJS.createRuntimeWithEventQueue() }

    internal val jsContext: JSContext by lazy {
        jsRuntime.createContext().apply {
            addPlugin(WukongConsolePlugin())
            addJavascriptInterface(WukongBridge(), "Wukong")
        }
    }

    fun initGlobalBridges() {
        MagicCube.starter.globalBridges.forEach {
            jsContext.addJavascriptInterface(it.value, it.key)
        }
    }

    companion object {
        val INSTANCE: MainJSEngine by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { MainJSEngine() }

        fun postJSQueue(runnable: Runnable) {
            INSTANCE.jsContext.postEventQueue(runnable)
        }
    }

    fun createJSObjectByMap(map: Map<String, Any?>?): JSObject {
        return JSHelper.map2JSObject(map, jsContext)
    }

    fun executeObjectScript(script: String): JSObject {
        val fileName = EncryptUtils.encryptMD5ToString(script)
        return jsContext.executeObjectScript(script, "${fileName}.js")
    }
}