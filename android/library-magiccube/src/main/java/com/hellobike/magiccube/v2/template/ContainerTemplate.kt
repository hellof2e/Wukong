package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.model.contractmodel.LayoutViewModel

open class ContainerTemplate(private val layout: LayoutViewModel) : Template(layout) {

    override fun doScan() {
        super.doScan()
        fillTemplate(layout.backgroundImage)
        fillTemplate(layout.activeBackgroundImage)
    }
}