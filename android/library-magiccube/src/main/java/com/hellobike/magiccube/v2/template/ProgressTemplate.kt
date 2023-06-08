package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.model.contractmodel.ProgressViewModel

class ProgressTemplate(private val progress: ProgressViewModel) : Template(progress) {

    override fun doScan() {
        super.doScan()
        fillTemplate(progress.progressColor)
        fillTemplate(progress.maxProgress)
        fillTemplate(progress.progress)
    }
}