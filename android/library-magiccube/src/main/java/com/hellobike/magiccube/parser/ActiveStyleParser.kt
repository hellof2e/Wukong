package com.hellobike.magiccube.parser

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.ActiveStyleViewModel
import com.hellobike.magiccube.model.scanProperty
import java.lang.Exception


/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/2 8:08 PM
 */
object ActiveStyleParser:BaseParser<ActiveStyleViewModel>(){
    init{
        scanProperty(ActiveStyleViewModel::class,this)
    }
    override fun parseElement(json: JSONObject): ActiveStyleViewModel {
        val style = ActiveStyleViewModel()
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