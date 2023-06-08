package com.hellobike.magiccube.model.contractmodel

import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.property.IntProperty
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.parser.StyleParser

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/2 8:27 PM
 */
@TargetParser(StyleParser::class)
class StyleViewModel {

    @StringProperty
    var background:String? = null
    @StringProperty
    var borderStyle:String? =null
    @StringProperty
    var borderWidth:String? =null
    @StringProperty
    var borderColor:String? =null
    @StringProperty
    var borderRadius:String? =null
    @StringProperty
    var opacity:String? = null

    private var borderRadiusArray: List<String>? = null

    fun borderRadiusArray(): List<String> {
        var arr = borderRadiusArray
        if (arr != null) return arr

        arr = if (!borderRadius.isNullOrBlank()) {
            val strArr = borderRadius?.trim()?.split(" ")?.map { it.trim() }
            if (strArr?.size == 4) {
                strArr
            } else {
                ArrayList(0)
            }
        } else {
            ArrayList(0)
        }

        borderRadiusArray = arr
        return arr
    }
}