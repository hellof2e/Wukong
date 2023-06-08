package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.model.contractmodel.TextViewModel

class TextTemplate(private val text: TextViewModel) : Template(text) {

    override fun doScan() {
        super.doScan()
        fillTemplate(text.maxRows)

        text.text.forEach {
            fillTemplate(it.color)
            fillTemplate(it.content)
            fillTemplate(it.fontSize)
            fillTemplate(it.fontWeight)
            fillTemplate(it.url)
            fillTemplate(it.width)
            fillTemplate(it.height)
        }
    }
}