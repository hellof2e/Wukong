package com.hellobike.magiccube.model.contractmodel

import android.content.Context
import android.graphics.Typeface
import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.parser.RichTextParser
import com.hellobike.magiccube.utils.TypefaceUtils

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/3/12 8:46 PM
 */
@TargetParser(RichTextParser::class)
class RichTextModel:BaseViewModel(),Cloneable {
    @StringProperty
    var fontSize:String? = null
    @StringProperty
    var color:String? = null
    @StringProperty
    var content:String? = null
    @StringProperty
    var fontWeight:String? = null

    @StringProperty
    var textDecorationLine: String? = null

    @StringProperty
    var fontFamily: String? = null

    @StringProperty
    var aFontFamily: String? = null // android 字体

    @StringProperty
    var url: String? = null // 图片url
    @StringProperty
    var width: String? = null // 宽度
    @StringProperty
    var height: String? = null // 高度


    fun hasFontFamily(): Boolean {
        return !aFontFamily.isNullOrBlank() || !fontFamily.isNullOrBlank()
    }

    fun findTypeface(context: Context): Typeface? {
        if (!aFontFamily.isNullOrBlank()) {
            return TypefaceUtils.parseTypeface(context, aFontFamily)
        }
        return TypefaceUtils.parseTypeface(context, fontFamily)
    }

    fun cloneInstance():RichTextModel{

        return clone() as RichTextModel
    }
}