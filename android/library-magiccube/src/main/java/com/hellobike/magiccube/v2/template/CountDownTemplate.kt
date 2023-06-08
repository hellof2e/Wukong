package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.model.contractmodel.LayoutViewModel

class CountDownTemplate(val layout: LayoutViewModel) : ContainerTemplate(layout) {

    override fun doScan() {
        super.doScan()
        fillTemplate(layout.clockOffset)
        fillTemplate(layout.deadline)
        fillTemplate(layout.interval)
        fillTemplate(layout.step)
        fillTemplate(layout.stop)
        fillTemplate(layout.countingType)
    }

}