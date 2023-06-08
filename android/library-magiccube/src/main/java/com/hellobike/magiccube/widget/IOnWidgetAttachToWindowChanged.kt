package com.hellobike.magiccube.widget

interface IOnWidgetAttachToWindowChanged {

    fun onAttachedToWindow()

    fun onDetachedFromWindow()

    fun onVisibilityChanged(isVisibility: Boolean)
}