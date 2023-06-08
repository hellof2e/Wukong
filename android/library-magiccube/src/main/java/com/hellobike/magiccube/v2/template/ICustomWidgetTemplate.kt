package com.hellobike.magiccube.v2.template

interface ICustomWidgetTemplate {

    fun getData(): Any?

    fun getScopeData(): Any?

    fun getScopeIndex(): Int
}