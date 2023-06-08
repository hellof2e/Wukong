package com.hellobike.magiccube.model.contractmodel

import android.content.Context
import android.graphics.Typeface
import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.parser.TextParser
import com.hellobike.magiccube.utils.TypefaceUtils

@TargetParser(TextParser::class)
class SpanViewModel : BaseViewModel() {

    @StringProperty
    var maxRows: String? = null

    @StringProperty
    var fontFamily: String? = null // 字体

    var text: RichTextModel? = null // 全量内容

    @StringProperty
    var aFontFamily: String? = null // android 字体

    var keywords: ArrayList<RichTextModel> = ArrayList() // 关键字内容

    fun findTypeface(context: Context): Typeface? {
        if (!aFontFamily.isNullOrBlank()) {
            return TypefaceUtils.parseTypeface(context, aFontFamily)
        }
        return TypefaceUtils.parseTypeface(context, fontFamily)
    }

    fun cloneInstance(): SpanViewModel {
        val textModel = SpanViewModel()

        textModel.maxRows = maxRows

        textModel.fontFamily = this.fontFamily

        textModel.text = text?.cloneInstance()

        for (keyword in keywords) {
            textModel.keywords.add(keyword.cloneInstance())
        }

        textModel.fontFamily = fontFamily

        return textModel
    }
}