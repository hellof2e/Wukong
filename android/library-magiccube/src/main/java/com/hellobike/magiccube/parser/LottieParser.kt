package com.hellobike.magiccube.parser

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.LottieViewModel
import com.hellobike.magiccube.model.scanProperty
import com.hellobike.magiccube.v2.ext.logt
import java.lang.Exception

object LottieParser: BaseParser<LottieViewModel>() {
    init{
        scanProperty(LottieViewModel::class,this)
    }

    override fun parseElement(ele:JSONObject): LottieViewModel {
        val lottie = LottieViewModel()
        try{
            val propMap = parserMap[this]
            if (propMap != null) {
                fillModel(lottie,ele,propMap)
            }
        }
        catch (e:Exception){
            e.printStackTrace()
            logt(e)
        }
        return lottie
    }
}