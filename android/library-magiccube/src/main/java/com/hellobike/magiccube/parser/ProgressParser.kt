package com.hellobike.magiccube.parser

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.ProgressViewModel
import com.hellobike.magiccube.model.scanProperty
import java.lang.Exception

object ProgressParser : BaseParser<ProgressViewModel>() {

    init {
        scanProperty(ProgressViewModel::class, this)
    }

    override fun parseElement(ele: JSONObject): ProgressViewModel {
        val progress = ProgressViewModel()
        try {
            val propMap = parserMap[this]
            if (propMap != null) {
                LottieParser.fillModel(progress, ele, propMap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return progress
    }
}