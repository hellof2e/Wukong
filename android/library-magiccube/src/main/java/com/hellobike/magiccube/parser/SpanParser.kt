package com.hellobike.magiccube.parser

import com.hellobike.magiccube.model.contractmodel.SpanViewModel
import com.hellobike.magiccube.model.scanProperty
import com.alibaba.fastjson.JSONObject
import java.lang.Exception

object SpanParser : BaseParser<SpanViewModel>() {

    init {
        scanProperty(SpanViewModel::class, this)
    }

    override fun parseElement(json: JSONObject): SpanViewModel {
        val text = SpanViewModel()
        try {
            val propMap = parserMap[this]
            if (propMap != null) {
                fillModel(text, json, propMap)
            }

            json.getJSONObject("text")?.let {
                text.text = RichTextParser.parseElement(it)
            }

            json.getJSONArray("keywords")?.forEach {
                val richText = RichTextParser.parseElement(it as JSONObject)
                text.keywords.add(richText)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return text
    }
}