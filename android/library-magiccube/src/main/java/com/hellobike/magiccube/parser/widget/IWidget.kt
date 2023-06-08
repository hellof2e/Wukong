package com.hellobike.magiccube.parser.widget

import android.view.View
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.model.contractmodel.StyleViewModel
import com.hellobike.magiccube.parser.engine.INodeAdapter
import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.template.Template

interface IWidget {
    /**
     * 当前 widget 维护的真正的 view
     */
    fun getView(): View

    fun getParentWidget(): ContainerWidget?

    fun attachToParent(parent: ContainerWidget?)

    fun getNodeAdapter(): INodeAdapter?

    fun applyLayout(layout: LayoutViewModel)

    fun applyStyle(style: StyleViewModel)

    fun doRender(baseViewModel: BaseViewModel, template: Template)

    fun bindTemplate(template: Template)

    fun bindCardContext(context: CardContext?)

    fun removeViewFromParent()

    fun markDirty()

    fun traceShow(template: Template, baseViewModel: BaseViewModel)

}