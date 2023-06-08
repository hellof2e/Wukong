package com.hellobike.magiccube.v2.node

import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.data.ScopeData
import com.hellobike.magiccube.v2.render.IRender
import java.lang.IllegalStateException

interface IVNodeEngine {

    fun getRender(): IRender

    fun injectCardContext(context: CardContext)

    @Throws(IllegalStateException::class)
    fun parse(viewModel: LayoutViewModel, data: Data): VNode

    @Throws(IllegalStateException::class)
    fun parseWithScopeData(viewModel: LayoutViewModel, data: Data, scopeData: ScopeData): VNode

    @Throws(IllegalStateException::class)
    fun diff(vNode: VNode, data: Data)

    @Throws(IllegalStateException::class)
    fun diffWithScopeData(vNode: VNode, data: Data, scopeData: ScopeData)
}