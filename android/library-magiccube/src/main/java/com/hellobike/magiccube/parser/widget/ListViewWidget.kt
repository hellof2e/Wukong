package com.hellobike.magiccube.parser.widget

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.v2.REASON_DEFAULT
import com.hellobike.magiccube.v2.template.ListViewTemplate
import com.hellobike.magiccube.v2.template.Template
import com.hellobike.magiccube.widget.BorderRecyclerView

class ListViewWidget(context: Context) : ContainerWidget(context) {

    private val adapter = RecyclerViewAdapter()

    private var layoutManager: LinearLayoutManager? = null

    override fun initView(): ViewGroup = BorderRecyclerView(context)

    override fun render(baseViewModel: BaseViewModel, template: Template) {
        super.render(baseViewModel, template)

        if (baseViewModel is LayoutViewModel && template is ListViewTemplate && template.hasChanged) {
            val orientation = baseViewModel.orientation

            val layoutManagerTemp = layoutManager ?: if (orientation == "column") {
                WKLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            } else {
                WKLinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }
            layoutManager = layoutManagerTemp


            val recyclerView = getView() as RecyclerView
            recyclerView.layoutManager = layoutManagerTemp
            recyclerView.itemAnimator = null

            val lastAdapter = recyclerView.adapter
            if (lastAdapter == null || lastAdapter !is RecyclerViewAdapter || lastAdapter != adapter) {
                recyclerView.adapter = adapter
            } else if (template.getCardContext()?.getRenderReason() == REASON_DEFAULT) {
                recyclerView.adapter = adapter
            }

            adapter.bindItemTypeVMMap(template.itemTypeVMMap)
            adapter.bindCardContext(template.getCardContext())
            adapter.bindWholeData(template.getWholeData())
            adapter.resetDataList(template.scopeDataList)
        }
    }
}