package com.hellobike.magiccube.utils

import android.content.Context
import android.graphics.Typeface
import com.hellobike.magiccube.v2.ext.logt
import java.util.concurrent.ConcurrentHashMap

object TypefaceUtils {

    private val typefaceMap: ConcurrentHashMap<String, Typeface> = ConcurrentHashMap()

    fun parseTypeface(context: Context, font: String?): Typeface? {
        if (font.isNullOrBlank()) return null
        val typeface = typefaceMap[font]
        if (typeface != null) return typeface
        try {
            val createFromAsset = Typeface.createFromAsset(context.assets, "${font}.ttf")
            typefaceMap[font] = createFromAsset
            return createFromAsset
        }catch (t: Throwable) {
            logt(t)
        }
        return null
    }
}