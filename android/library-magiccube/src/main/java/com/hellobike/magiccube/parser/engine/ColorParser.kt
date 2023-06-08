package com.hellobike.magiccube.parser.engine

import android.graphics.Color
import androidx.annotation.ColorInt

object ColorParser {

    /**
     * @param colorStr #rrggbbaa
     */
    @ColorInt
    fun parseColor(colorStr: String): Int {
        try {
            return if (colorStr.isNotBlank() && colorStr.startsWith("#") && colorStr.length == 9) {
                // #rrggbbaa
                val rgb: String = colorStr.substring(colorStr.length - 8, colorStr.length - 2)
                val alpha = colorStr.substring(colorStr.length - 2)
                Color.parseColor("#$alpha$rgb")
            } else {
                Color.parseColor(colorStr)
            }
        }catch (t: Throwable) {
            t.printStackTrace()
        }
        return Color.TRANSPARENT
    }
}