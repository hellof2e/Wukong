package com.hellobike.magiccube.model

import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.StyleManager
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.parser.DSLParser
import java.io.Serializable
import java.lang.Exception
import kotlin.jvm.Throws

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/3/17 2:53 PM
 */
class StyleModel:Serializable {
    var fromCache: Boolean = false
    var layoutModel: LayoutViewModel? = null
    var styleString: String? = null
    var styleUrl: String? = null
    var errorCode = 0

    // style json 是否有效
    fun isValid(): Boolean {
        return errorCode == 0 && !styleUrl.isNullOrBlank() && !styleString.isNullOrBlank()
    }


    // view model 是否解析完成
    fun isValidViewModel(): Boolean = isValid() && layoutModel != null


    // 解析 ViewModel
    @Throws(Exception::class)
    fun parseViewModel(): LayoutViewModel? {
        val json = styleString
        if (json.isNullOrBlank()) throw IllegalStateException("ViewModel 解析失败！json 为空")

        try {
            val jsonOb = JSONObject.parseObject(json)
            return if (jsonOb !is JSONObject) {
                throw IllegalStateException("ViewModel 解析失败！jsonOb 不是 JSONObject")
            } else {
                // 根据json解析 view model 树
                DSLParser.parseRootLayout(jsonOb)
            }
        } catch (e: Exception) {
            StyleManager.removeStyleCache(styleUrl)
            throw e
        }
    }
}