package com.hellobike.magiccube.v2.node

import com.hellobike.magiccube.model.contractmodel.BaseViewModel

class VIfNode(baseViewModel: BaseViewModel, type: String) : VNode(baseViewModel, type) {


    override fun scanTemplate() {
        template.scanIf()
    }
}