package com.hellobike.magiccube.v2.template

import java.util.regex.Pattern

object Grammar {

    private const val TEMPLATE_START = "\${"
    private const val TEMPLATE_END = "}"

    private const val FUNCTION = "\$f{"
    private const val EXPRESSION_B_START = "\$fb{"
    private const val EXPRESSION_I_START = "\$fi{"
    private const val EXPRESSION_F_START = "\$ff{"
    private const val EXPRESSION_D_START = "\$fd{"
    private const val EXPRESSION_S_START = "\$fs{"

//    private const val VALUE_REGEXP = "(\\\$\\{[\\w\\.]+\\})"
//    private const val VALUE_REGEXP = "(\\\$\\{[\\w\\.\\[\\]]+\\})"
    private const val VALUE_REGEXP = "(\\\$\\{[\\w\\.\\[\\]()]+\\})"
    private const val ARRAY_REGEXP = "^(\\w+)\\[(\\d+)\\]$"

//    private const val FUNCTION_REGEXP = "^(?<methodName>\\w+)\\((?<args>.*)\\)$"
    private const val FUNCTION_REGEXP = "^(\\w+)\\((.*)\\)$"

    val PATTERN_EXPRESSION by lazy { Pattern.compile(VALUE_REGEXP) }
    val PATTERN_ARRAY by lazy { Pattern.compile(ARRAY_REGEXP) }
    val PATTERN_FUNCTION by lazy { Pattern.compile(FUNCTION_REGEXP) }

    // 截取 field 模版。例如：${obj1.obj2.obj3} -> obj1.obj2.obj3
    fun formatFieldTemplate(template: String): String {
        return if (isFieldTemplate(template)) {
            template.substring(TEMPLATE_START.length, template.length - TEMPLATE_END.length)
        } else {
            template
        }
    }

    // 截取函数模版。例如：$fb{${num} == 0} -> ${num} == 0
    fun formatExpressionTemplate(template: String): String {
        return if (isExpressionTemplate(template)) {
            template.substring(4, template.length - TEMPLATE_END.length)
        } else {
            template
        }
    }

    // 判断是否是 field 模版
    fun isFieldTemplate(template: String?): Boolean {
        if (template.isNullOrBlank()) return false
        return template.startsWith(TEMPLATE_START) && template.endsWith(TEMPLATE_END)
    }

    // 判断是否是函数模版
    fun isExpressionTemplate(template: String?): Boolean {
        if (template.isNullOrBlank()) return false
        return template.startsWith(EXPRESSION_B_START)
                || template.startsWith(EXPRESSION_I_START)
                || template.startsWith(EXPRESSION_F_START)
                || template.startsWith(EXPRESSION_D_START)
                || template.startsWith(EXPRESSION_S_START)
    }

    fun isFunction(template: String?): Boolean {
        if (template.isNullOrBlank()) return false
        return template.startsWith(FUNCTION)
    }

    fun isBoolExpression(template: String?): Boolean {
        if (template.isNullOrBlank()) return false
        return template.startsWith(EXPRESSION_B_START)
    }
}