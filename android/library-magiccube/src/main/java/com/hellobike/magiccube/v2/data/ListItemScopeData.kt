package com.hellobike.magiccube.v2.data

class ListItemScopeData(data: Any?, index: Int, itemKey: String?) : ScopeData(data, index) {

    val itemType: Int by lazy { (parseFieldTemplate(itemKey).stringValue() ?: "").hashCode() }

    val key: String by lazy { parseFieldTemplate(itemKey).stringValue() ?: "" }
}