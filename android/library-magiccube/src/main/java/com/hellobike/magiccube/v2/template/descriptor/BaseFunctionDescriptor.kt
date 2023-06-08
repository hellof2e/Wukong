package com.hellobike.magiccube.v2.template.descriptor

import com.alibaba.fastjson.JSONArray
import com.hellobike.magiccube.parser.DSLParser
import com.hellobike.magiccube.v2.js.JSHelper
import com.hellobike.magiccube.v2.js.MainJSEngine
import com.hellobike.magiccube.v2.js.wrapper.WKJSArray
import com.hellobike.magiccube.v2.reports.session.SessionResult

open class BaseFunctionDescriptor {

    var funcName: String = ""
    var args: List<Any?>? = null

    protected fun reportInvokeFailed(t: Throwable) {
        val argsJson = if (args.isNullOrEmpty()) "" else try {
            JSONArray.toJSONString(args)
        } catch (t: Throwable) {
            ""
        }
    }

    protected fun convertArgs(): WKJSArray? {
        val args = args
        return if (!args.isNullOrEmpty()) {
            WKJSArray(
                MainJSEngine.INSTANCE.jsContext,
                null,
                JSHelper.list2JSArray(args, MainJSEngine.INSTANCE.jsContext)
            )
        } else {
            null
        }
    }

}