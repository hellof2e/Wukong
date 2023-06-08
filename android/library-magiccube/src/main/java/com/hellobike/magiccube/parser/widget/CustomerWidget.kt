package com.hellobike.magiccube.parser.widget

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.v2.template.CustomWidgetTemplate
import com.hellobike.magiccube.v2.template.Template
import com.hellobike.magiccube.widget.BaseCustomWidget
import com.hellobike.magiccube.widget.CustomerWidgetContainer
import com.hellobike.magiccube.widget.IOnWidgetAttachToWindowChanged
import com.hellobike.magiccube.widget.IWKCustomWidget

class CustomerWidget(
    context: Context,
    private val type: String,
    private val widget: BaseCustomWidget
) : BaseWidget<CustomerWidgetContainer>(context), IOnWidgetAttachToWindowChanged {
    private var hasRender = false
    private val customWidgetTemplate by lazy { CustomWidgetTemplate() }

    override fun initView(): CustomerWidgetContainer {
        val customerWidgetContainer = CustomerWidgetContainer(context)
        customerWidgetContainer.setWidgetAttachToWindowChanged(this)
        val view = widget.initView(context, type)
        if (view.parent is ViewGroup) {
            (view.parent as ViewGroup).removeView(view)
        }
        if (customerWidgetContainer.childCount > 0) {
            customerWidgetContainer.removeAllViews()
        }
        customerWidgetContainer.addView(
            view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
        )

        return customerWidgetContainer
    }


    override fun doRender(baseViewModel: BaseViewModel, template: Template) {
        hasRender = false
        super.doRender(baseViewModel, template)
        if (!hasRender) {
            render(baseViewModel, template)
        }
    }
    override fun render(baseViewModel: BaseViewModel, template: Template) {
        super.render(baseViewModel, template)
        widget.handler = template.getCardContext()?.handler
        customWidgetTemplate.setTemplate(template)
        widget.render(customWidgetTemplate)
        hasRender = true
    }

    override fun onAttachedToWindow() {
        widget.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        widget.onDetachedFromWindow()
    }

    override fun onVisibilityChanged(isVisibility: Boolean) {
        widget.onVisibilityChanged(isVisibility)
    }
}