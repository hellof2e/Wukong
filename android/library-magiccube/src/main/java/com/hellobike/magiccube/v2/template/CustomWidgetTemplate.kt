package com.hellobike.magiccube.v2.template

import java.lang.ref.WeakReference

class CustomWidgetTemplate : ICustomWidgetTemplate {

    private var templateWeak: WeakReference<Template>? = null

    fun setTemplate(template: Template) {
        templateWeak = WeakReference(template)
    }

    override fun getData(): Any? = templateWeak?.get()?.getWholeData()?.originData()

    override fun getScopeData(): Any? = templateWeak?.get()?.getScopeData()?.originData()

    override fun getScopeIndex(): Int = templateWeak?.get()?.getScopeData()?.index ?: -1

}