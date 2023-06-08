package com.hellobike.magiccube.parser

import android.os.Build
import androidx.annotation.RequiresApi
import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.ImageViewModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import java.lang.Exception
import com.hellobike.magiccube.model.scanProperty


/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/2 8:08 PM
 */
object LayoutParser:BaseParser<LayoutViewModel>() {
    init{
        scanProperty(LayoutViewModel::class,this)
    }
    override fun parseElement(json: JSONObject): LayoutViewModel {
        val layout = LayoutViewModel()
        try{
            if (json.containsKey("engine"))
                layout.engine = json.getString("engine")

            val propMap = parserMap[this]
            if (propMap != null) {
                fillModel(layout,json,propMap)
            }
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        return layout
    }
}