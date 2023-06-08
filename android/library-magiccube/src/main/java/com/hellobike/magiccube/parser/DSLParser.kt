package com.hellobike.magiccube.parser

import android.app.Activity
import android.os.Build
import android.text.*
import android.text.style.CharacterStyle
import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.*
import com.hellobike.magiccube.model.contractmodel.*
import com.hellobike.magiccube.v2.configs.Constants

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/4 11:08 AM
 */
object DSLParser {

    var density: Float? = null
    var scaleDensity: Float? = null
    var widthScale = 0f
    var openWebBlock: ((url: String) -> Unit)? = null

    fun parseRootLayout(jsonOb: JSONObject): LayoutViewModel? {
        return try {
            parseLayoutGroup(jsonOb) as LayoutViewModel
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseLayoutGroup(jsonOb: JSONObject): BaseViewModel {
        val optModel: BaseViewModel
        when (jsonOb["type"] as String) {
            "container", Constants.NODE_COUNTING, Constants.NODE_LIST_VIEW, Constants.NODE_LIST_ITEM -> {
                optModel = LayoutParser.parseElement(jsonOb)
                if (jsonOb.containsKey("children")) {
                    optModel.childList = ArrayList()
                    val array = jsonOb.getJSONArray("children")
                    for (ob in array) {
                        val child = parseLayoutGroup(ob as JSONObject)
                        optModel.childList?.add(child)
                    }
                }
            }
            "img" ->
                optModel = ImageParser.parseElement(jsonOb)
            "text" ->
                optModel = TextParser.parseElement(jsonOb)
            "lottie" ->
                optModel = LottieParser.parseElement(jsonOb)
            "span" ->
                optModel = SpanParser.parseElement(jsonOb)
            "progress" ->
                optModel = ProgressParser.parseElement(jsonOb)
            else ->
                optModel = CustomerParser.parseElement(jsonOb)
        }
        if (jsonOb.containsKey("type"))
            optModel.type = jsonOb.getString("type")
        if (jsonOb.containsKey("layout"))
            optModel.layout = LayoutParser.parseElement(jsonOb.getJSONObject("layout"))
        if (jsonOb.containsKey("style"))
            optModel.style = StyleParser.parseElement(jsonOb.getJSONObject("style"))
        if (jsonOb.containsKey("action"))
            optModel.action = ActionParser.parseElement(jsonOb.getJSONObject("action"))
        if (jsonOb.containsKey("active-style"))
            optModel.activeStyle =
                ActiveStyleParser.parseElement(jsonOb.getJSONObject("active-style"))
        if (jsonOb.containsKey("m-for"))
            optModel.mFor = jsonOb.getString("m-for")
        if (jsonOb.containsKey("m-if"))
            optModel.mIf = jsonOb.getString("m-if")
        if (jsonOb.containsKey("logic"))
            optModel.logic = jsonOb.getString("logic")
        if (jsonOb.containsKey("node-id"))
            optModel.nodeId = jsonOb.getString("node-id")

        if (optModel.layout == null) {
            optModel.layout = LayoutViewModel()
        }
        return optModel
    }

    fun String.dimen2px(): Int {
        var value = 0
        if (endsWith("rpx")) {
            value = (substring(0, indexOf("rpx")).toFloat() * widthScale).toInt()
        } else if (endsWith("px")) {
            value = (substring(0, indexOf("px")).toFloat() * density!! + 0.5f).toInt()
        }
        return if (density == null)
            0
        else
            value
    }


    fun String.parseValue(): MagicValue {
        var magicValue: MagicValue = MagicValue.ZERO
        if (endsWith("rpx")) {
            val value = (substring(0, indexOf("rpx")).toFloat() * widthScale)
            magicValue = MagicValue(value * 1.0f, MagicUnit.PIXEL)
        } else if (endsWith("px")) {
            val value = (substring(0, indexOf("px")).toFloat() * density!! + 0.5f)
            magicValue = MagicValue(value * 1.0f, MagicUnit.PIXEL)
        } else if (equals("100%", true)) {
            magicValue = MagicValue.PERCENT_100
        } else if (endsWith("%")) {
            val value = substring(0, indexOf("%")).toFloat()
            magicValue = MagicValue(value, MagicUnit.PERCENT)
        }

        return if (density == null)
            MagicValue.ZERO
        else
            magicValue
    }


    class FakeBoldSpan: CharacterStyle(){
        override fun updateDrawState(tp: TextPaint?) {
            tp?.isFakeBoldText = true
        }
    }


    /**
     * 判断Activity是否Destroy
     * @param activity
     * @return
     */
    fun isDestroy(activity: Activity?): Boolean {
        return activity == null || activity.isFinishing || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed)
    }
}


