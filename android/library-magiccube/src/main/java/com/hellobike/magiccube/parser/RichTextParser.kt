package com.hellobike.magiccube.parser

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.RichTextModel
import com.hellobike.magiccube.model.scanProperty
import java.lang.Exception

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/3/15 4:34 PM
 */
object RichTextParser:BaseParser<RichTextModel>() {
    init{
        scanProperty(RichTextModel::class,this)
    }
    override fun parseElement( json: JSONObject): RichTextModel {
        val text = RichTextModel()
        try{
            val propMap = parserMap[this]
            if (propMap != null) {
                TextParser.fillModel(text, json, propMap)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return text
    }
}