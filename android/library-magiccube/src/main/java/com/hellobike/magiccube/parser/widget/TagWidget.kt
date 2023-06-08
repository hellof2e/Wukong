package com.hellobike.magiccube.parser.widget

import android.content.Context
import android.view.View
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.v2.template.Template

class TagWidget(context: Context) : BaseWidget<View>(context) {
    override fun initView(): View = View(context).apply {
        visibility = View.GONE
    }

    override fun render(baseViewModel: BaseViewModel, template: Template) {
        // do nothing
    }
}