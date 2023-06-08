package com.hellobike.magiccube.parser

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.ImageViewModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.model.scanProperty
import java.lang.Exception

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/1/28 2:04 PM
 */
object ImageParser: BaseParser<ImageViewModel>() {
    init{
        scanProperty(ImageViewModel::class,this)
    }

    override fun parseElement(ele:JSONObject): ImageViewModel {
        val image = ImageViewModel()
        try{
            val propMap = parserMap[this]
            if (propMap != null) {
                fillModel(image,ele,propMap)
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return image
    }
}