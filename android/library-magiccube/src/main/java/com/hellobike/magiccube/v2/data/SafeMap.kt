package com.hellobike.magiccube.v2.data

class SafeMap(val value: Any?) {

    fun stringValue(): String? = if (this.value == null) null else if (this.value is String) this.value else this.value.toString()

    private fun numberValue(): Number? = if (this.value is Number) this.value else null

    fun longValue(): Long? = tryOrNull {
        if (this.value is Number) {
            numberValue()?.toLong()
        } else {
            stringValue()?.toLongOrNull()
        }
    }

    fun intValue(): Int? = tryOrNull {
        if (this.value is Number) {
            numberValue()?.toInt()
        } else {
            stringValue()?.toIntOrNull()
        }
    }

    fun floatValue(): Float? = tryOrNull {
        if (this.value is Number) {
            numberValue()?.toFloat()
        } else {
            stringValue()?.toFloatOrNull()
        }
    }

    fun doubleValue(): Double? = tryOrNull {
        if (this.value is Number) {
            numberValue()?.toDouble()
        } else {
            stringValue()?.toDoubleOrNull()
        }
    }

    fun boolValue(): Boolean? = tryOrNull {
        if (this.value is Boolean) {
            this.value
        } else {
            stringValue()?.toBoolean()
        }
    }

    fun mapValue(): Map<*, *>? = if (this.value is Map<*, *>) this.value else null
    fun listValue(): List<*>? = if (this.value is List<*>) this.value else null

    fun hashMapValue(): HashMap<*, *>? = if (this.value is HashMap<*, *>) this.value else null

    fun mutableMapMapValue(): MutableMap<*, *>? = if (this.value is MutableMap<*, *>) this.value else null

    fun mapValueOrBlank(): Map<*, *> = if (this.value is Map<*, *>) this.value else HashMap<Any, Any>()
    fun listValueOrBlank(): List<*> = if (this.value is List<*>) this.value else ArrayList<Any>()

    override fun toString(): String {
        return "<SafeMap:$value>"
    }

    private fun <T> tryOrNull(block: () -> T?): T? {
        try {
            return block.invoke()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return null
    }
}
