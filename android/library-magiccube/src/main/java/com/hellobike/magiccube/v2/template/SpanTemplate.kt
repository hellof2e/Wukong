package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.model.contractmodel.SpanViewModel

class SpanTemplate(private val span: SpanViewModel) : Template(span) {

    override fun doScan() {
        super.doScan()

        fillTemplate(span.text?.color)
        fillTemplate(span.text?.content)
        fillTemplate(span.text?.fontSize)
        fillTemplate(span.text?.fontWeight)

        span.keywords.forEach {
            fillTemplate(it.color)
            fillTemplate(it.content)
            fillTemplate(it.fontSize)
            fillTemplate(it.fontWeight)
        }

        fillTemplate(span.maxRows)
    }
}