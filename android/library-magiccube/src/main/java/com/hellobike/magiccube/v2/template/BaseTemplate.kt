package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.configs.Constants
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.data.SafeMap
import com.hellobike.magiccube.v2.data.ScopeData
import com.hellobike.magiccube.v2.template.descriptor.JSFuncDescriptor

open class BaseTemplate {

    private val mTemplateValueMap: HashMap<String, SafeMap> = HashMap()

    private var wholeData: Data? = null
    private var scopeData: ScopeData? = null

//    private var jsEngine: IJsEngine? = null

    private var cardContext: CardContext? = null

    var hasChanged = false

//    // 绑定 js 引擎
//    internal fun bindJsEngine(jsEngine: IJsEngine) {
//        this.jsEngine = jsEngine
//    }

    internal fun getCardContext() = cardContext

    internal fun bindCardContext(cardContext: CardContext) {
        this.cardContext = cardContext
    }

    // 绑定全量数据
    internal fun bindWholeData(data: Data) {
        this.wholeData = data
    }

    // 绑定域数据
    internal fun bindScopeData(data: ScopeData?) {
        this.scopeData = data
    }

    fun getScopeData() = scopeData
    fun getWholeData() = wholeData

//    fun getJsEngine() = jsEngine

    fun getJsEngine() = cardContext?.jsEngine


    private fun findData(template: String): Data? {
        val formatTemplate = Grammar.formatFieldTemplate(template)
        return if (!scopeData?.scopePrefixName.isNullOrBlank()
            && formatTemplate.startsWith(scopeData?.scopePrefixName!!)
        ) {
            // 首先看前缀是不是自己的作用域，如果是自己，则可以直接使用自己的作用域数据
            scopeData
        } else if (!scopeData?.scopeIndexPrefixName.isNullOrBlank()
            && formatTemplate == scopeData?.scopeIndexPrefixName
        ) {
            scopeData
        } else { // 作用域查找结束，如果还是没有找到，则可以直接使用全局数据
            wholeData
        }
    }

    private fun parseTemplate(template: String?): SafeMap {
        if (template.isNullOrBlank()) return Constants.NULL_VALUE
        val data = findData(template) ?: return Constants.NULL_VALUE
        return data.parseFieldTemplate(template)
    }

    fun getValue(template: String?): SafeMap {
        if (template.isNullOrBlank()) return Constants.NULL_VALUE
        return mTemplateValueMap[template] ?: Constants.NULL_VALUE
    }


    fun scanFunction(templateStr: String?): JSFuncDescriptor? {
        if (!Grammar.isFunction(templateStr)) return null

        // $f{  }

        var template = templateStr!!.trim()
        template = template.substring(3, template.length - 1).trim()

        val result = Grammar.PATTERN_FUNCTION.matcher(template)
        if (!result.find()) return null

        val methodNameStr = result.group(1)?.trim()

        if (methodNameStr.isNullOrBlank()) return null // 方法名是空

        val functionDescriptor = JSFuncDescriptor()
        functionDescriptor.funcName = methodNameStr

        val argsStr = result.group(2)?.trim()
        if (!argsStr.isNullOrBlank()) {
            // 有参数
            val argsStrArr = argsStr.split(",")
            val args = ArrayList<Any?>()
            argsStrArr.forEach {
                val key = it.trim()
                fillTemplate(key)
                val value = getValue(key).value
                args.add(value)
            }
            functionDescriptor.args = args
        }
        return functionDescriptor
    }


    private fun putValue(template: String, newValue: SafeMap) {
//        mTemplateValueMap[template] = newValue
        val oldValue = mTemplateValueMap.put(template, newValue)
        if (oldValue == null || oldValue.value != newValue.value) {
            hasChanged = true
        }
    }

    // template: ${num} ${listItem.num} $fb{bolo}
    protected fun fillTemplate(template: String?) {
        if (template.isNullOrBlank()) return
        when {
            Grammar.isFieldTemplate(template) -> {
                putValue(template, parseTemplate(template))
            }
            Grammar.isExpressionTemplate(template) -> {
                fillExpression(template)
            }
            else -> {
                putValue(template, SafeMap(template))
            }
        }
    }


    // 填充模版或者表达式
    protected fun fillTemplateAndExpression(template: String?) {
        if (template.isNullOrBlank()) return
        when {
            Grammar.isFieldTemplate(template) -> {
                putValue(template, parseTemplate(template))
            }
            Grammar.isExpressionTemplate(template) -> {
                fillExpression(template)
            }
            else -> {
                putValue(template, SafeMap(template))
            }
        }
    }

    // 尺寸是否发生改变
    fun hasSizeChanged(): Boolean = false


    protected fun scanDynamicMapData(template: Any?) {
        if (template is String) {
            fillTemplate(template)
        } else if (template is Map<*, *>) {
            scanMapData(template)
        }
    }


    /**
     * 获取动态数据类型
     * @param template 可以是一个String，也可以是一个 Map
     */
    fun getDynamicValue(template: Any?): SafeMap {
        if (template is String) {
            return getValue(template)
        } else if (template is Map<*, *>) {
            val map = getDynamicMapValue(template)
            return SafeMap(map)
        }
        return Constants.NULL_VALUE
    }

    private fun getDynamicMapValue(mapTemplate: Map<*, *>?): HashMap<String, Any?> {
        if (mapTemplate.isNullOrEmpty()) return HashMap()
        val result = HashMap<String, Any?>()
        mapTemplate.forEach { entry ->
            val key = entry.key
            if (key is String) {
                val value = entry.value
                if (value is String) { // String 可以直接取值
                    result[key] = getValue(value).value
                } else if (value is Map<*, *>) { // 如果是一个map，则需要递归取值
                    result[key] = getDynamicMapValue(value)
                }
            }
        }
        return result
    }

    // 深层遍历解析map数据
    private fun scanMapData(map: Map<*, *>) {
        map.forEach { entry ->
            val key = entry.key
            val value = entry.value
            if (key is String) {
                if (value is Map<*, *>) {
                    scanMapData(value)
                } else if (value is String) {
                    fillTemplate(value)
                }
            }
        }
    }

    // 填充并执行表达式
    protected fun fillExpression(template: String?) {

        var templateTemp = template ?: return
        if (!Grammar.isExpressionTemplate(templateTemp)) return

        val matcher = Grammar.PATTERN_EXPRESSION.matcher(templateTemp)

        var hasNull = false
        while (matcher.find()) {

            // $fb{map.bool}
            val matchResult = matcher.toMatchResult().group(0)

            if (Grammar.isFieldTemplate(matchResult)) {
                fillTemplate(matchResult)

                val value = getValue(matchResult).value
                templateTemp = if (value != null) {
                    templateTemp.replace(matchResult, value.toString(), false)
                } else {
                    hasNull = true
                    templateTemp.replace(matchResult, "null", false)
                }
            }
        }

        var value = getJsEngine()?.executeScriptGlobal(templateTemp)

        if (value == "null" && hasNull) {
            value = ""
        }

        // $fb{map.bool} : SafeMap(result)
        putValue(template, SafeMap(value))
    }
}