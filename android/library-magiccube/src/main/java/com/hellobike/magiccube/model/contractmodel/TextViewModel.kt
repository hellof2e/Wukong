package com.hellobike.magiccube.model.contractmodel

import android.content.Context
import android.graphics.Typeface
import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.property.IntProperty
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.parser.TextParser
import com.hellobike.magiccube.utils.TypefaceUtils

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/2 10:58 AM
 */
@TargetParser(TextParser::class)
class TextViewModel: BaseViewModel(){

    @StringProperty
    var maxRows: String? = null
    var text: ArrayList<RichTextModel> = ArrayList()

    @StringProperty
    var fontFamily: String? = null

    @StringProperty
    var aFontFamily: String? = null // android 字体

    fun findTypeface(context: Context): Typeface? {
        if (!aFontFamily.isNullOrBlank()) {
            return TypefaceUtils.parseTypeface(context, aFontFamily)
        }
        return TypefaceUtils.parseTypeface(context, fontFamily)
    }

    fun cloneInstance(): TextViewModel {
        val textModel = TextViewModel()
        textModel.maxRows = maxRows
        for (txt in text) {
            textModel.text.add(txt.cloneInstance())
        }
        textModel.fontFamily = fontFamily
        return textModel
    }
}