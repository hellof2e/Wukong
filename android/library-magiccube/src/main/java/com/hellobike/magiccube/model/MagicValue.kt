package com.hellobike.magiccube.model

import kotlin.math.roundToInt

enum class MagicUnit {
    PIXEL,
    PERCENT
}

class MagicValue(val value: Float, val unit: MagicUnit) {

    companion object {

        val ZERO = MagicValue(0.0f, MagicUnit.PIXEL)

        val PERCENT_100 = MagicValue(100.0f, MagicUnit.PERCENT)

        fun toInt(value: Float): Int = value.toInt()
    }

    fun roundInt(): Int = value.roundToInt()

    fun intValue(): Int = value.toInt()

    fun floatValue(): Float = value
}