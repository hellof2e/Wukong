package com.hellobike.magiccube.parser

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.contractmodel.ImageViewModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.model.contractmodel.StyleViewModel
import com.hellobike.magiccube.model.scanProperty
import java.lang.Exception


/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/2 8:08 PM
 */
object StyleParser:BaseParser<StyleViewModel>(){
    init{
        scanProperty(StyleViewModel::class,this)
    }
    override fun parseElement(json: JSONObject): StyleViewModel {
        val style = StyleViewModel()
        try{
            val propMap = parserMap[this]
            if (propMap != null) {
                fillModel(style,json,propMap)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return style
    }

}