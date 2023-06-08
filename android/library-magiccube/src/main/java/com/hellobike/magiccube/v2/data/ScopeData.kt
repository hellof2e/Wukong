package com.hellobike.magiccube.v2.data

open class ScopeData(data: Any?, val index: Int) : Data(data) {
    override fun parseListItemIndex(): Int = index
}