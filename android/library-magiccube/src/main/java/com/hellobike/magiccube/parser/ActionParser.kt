package com.hellobike.magiccube.parser

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.contractmodel.*
import java.lang.Exception


/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/2 8:08 PM
 */
object ActionParser:BaseParser<ActionViewModel>(){
    override fun parseElement(json: JSONObject): ActionViewModel {
        var action:ActionViewModel? = null
        try{
            action = JSONObject.parseObject(json.toString(),ActionViewModel::class.java)
        }
        catch (e: Exception){
            e.printStackTrace()
        }
        if(action==null)
            return ActionViewModel()
//        try {
//            action!!.click!!.report!!.busInfo = JSONObject.parseObject(action!!.click!!.report!!.busInfo.toString(),HashMap::class.java)
//        }
//        catch (e:Exception){
//            e.printStackTrace()
//        }

            return action
    }

}