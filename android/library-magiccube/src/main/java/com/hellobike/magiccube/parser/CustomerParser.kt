package com.hellobike.magiccube.parser

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.CustomerViewModel
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
object CustomerParser: BaseParser<CustomerViewModel>() {
    init{
        scanProperty(CustomerViewModel::class,this)
    }

    override fun parseElement(ele:JSONObject): CustomerViewModel {
        val custom = CustomerViewModel()
        try{
            val propMap = parserMap[this]
            if (propMap != null) {
                fillModel(custom,ele,propMap)
            }
        }
        catch (e:Exception){
            e.printStackTrace()
        }
        return custom
    }
}