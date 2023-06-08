package com.hellobike.magiccube.v2.template

import android.util.SparseArray
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.v2.data.ListItemScopeData

class ListViewTemplate(private val layout: LayoutViewModel) : Template(layout) {

    val scopeDataList: ArrayList<ListItemScopeData> = ArrayList()

    val itemTypeVMMap: SparseArray<LayoutViewModel> = SparseArray()

    override fun doScan() {
        super.doScan()
        fillTemplate(layout.listData)

        if (hasChanged) {
            val childList = layout.childList
            val listData = getValue(layout.listData).listValue()
            if (listData.isNullOrEmpty() || childList.isNullOrEmpty()) {
                scopeDataList.clear()
                itemTypeVMMap.clear()
            } else {
                scopeDataList.clear()
                itemTypeVMMap.clear()
                listData.forEachIndexed { index, item ->
                    val newScopeData = ListItemScopeData(item, index, layout.itemKey)
                    newScopeData.bindScopePrefixName(
                        layout.scopePrefixName, layout.scopeIndexPrefixName
                    )

                    scopeDataList.add(newScopeData)
                }
                childList.forEach {
                    if (it is LayoutViewModel) {
                        val itemType = (it.itemType ?: "").hashCode()
                        itemTypeVMMap.put(itemType, it)
                    }
                }
            }
        }
    }
}