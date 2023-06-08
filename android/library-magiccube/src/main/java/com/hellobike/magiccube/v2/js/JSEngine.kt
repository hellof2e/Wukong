package com.hellobike.magiccube.v2.js

import com.hellobike.magiccube.utils.tryCatch
import com.quickjs.JSArray
import com.quickjs.JSContext
import com.quickjs.JSObject
import com.quickjs.QuickJS

class JSEngine {

    private val jsRuntime: QuickJS by lazy { QuickJS.createRuntime() }
    private val jsContext: JSContext by lazy { jsRuntime.createContext() }

    companion object {
        private const val G_FILE_NAME = "global.js"

        val INSTANCE: JSEngine by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { JSEngine() }
    }

    fun init() {
        tryCatch {
            jsContext
        }
    }

    fun executeScriptGlobal(script: String?): Any? {
        if (script.isNullOrBlank()) return false
        return jsContext.executeScript(script, G_FILE_NAME)
    }

    fun executeBoolScriptGlobal(script: String?): Boolean {
        if (script.isNullOrBlank()) return false
        return jsContext.executeBooleanScript(script, G_FILE_NAME)
    }
}