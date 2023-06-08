package com.hellobike.magiccube.widget

import android.content.Context
import android.view.View
import com.hellobike.magiccube.v2.template.ICustomWidgetTemplate
import com.hellobike.magiccube.v2.template.Template

interface IWKCustomWidget {

    fun initView(context: Context, typeName: String): View

    fun render(template: ICustomWidgetTemplate)

    fun onAttachedToWindow()

    fun onDetachedFromWindow()

    fun onVisibilityChanged(isVisibility: Boolean)
}