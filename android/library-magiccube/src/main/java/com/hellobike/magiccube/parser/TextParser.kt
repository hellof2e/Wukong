package com.hellobike.magiccube.parser

import android.content.Context
import android.widget.ImageView
import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.ImageViewModel
import com.hellobike.magiccube.model.contractmodel.StyleViewModel
import com.hellobike.magiccube.model.contractmodel.TextViewModel
import com.hellobike.magiccube.model.scanProperty
import org.w3c.dom.Element
import java.lang.Exception

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/1/28 2:04 PM
 */
object TextParser: BaseParser<TextViewModel>() {
    init{
        scanProperty(TextViewModel::class,this)
    }
    override fun parseElement( json:JSONObject): TextViewModel {
        val text = TextViewModel()
        try{
            val propMap = parserMap[this]
            if (propMap != null) {
                fillModel(text,json,propMap)
            }
            for(textOb in json.getJSONArray("text")){
                val richText = RichTextParser.parseElement(textOb as JSONObject)
                text.text.add(richText)
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return text
    }
}