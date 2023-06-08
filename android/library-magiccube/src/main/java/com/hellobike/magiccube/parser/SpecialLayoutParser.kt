package com.hellobike.magiccube.parser

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.model.contractmodel.SpecialLayoutViewModel
import com.hellobike.magiccube.model.scanProperty
import java.lang.Exception

object SpecialLayoutParser: BaseParser<SpecialLayoutViewModel>() {

    init{
        scanProperty(LayoutViewModel::class,this)
    }

    override fun parseElement(json: JSONObject): SpecialLayoutViewModel {
        val layout = SpecialLayoutViewModel()
        try{

            val propMap = parserMap[this]
            if (propMap != null) {
                SpecialLayoutParser.fillModel(layout, json, propMap)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return layout
    }
}