package com.hellobike.magiccube.parser.widget

import android.content.Context
import android.util.SparseArray
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.util.putAll
import androidx.recyclerview.widget.RecyclerView
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.data.ListItemScopeData

class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class EmptyViewHolder(context: Context) : RecyclerView.ViewHolder(FrameLayout(context))

    private val scopeDataList: ArrayList<ListItemScopeData> = ArrayList()
    private val itemTypeVMMap: SparseArray<LayoutViewModel> = SparseArray()
    private var cardContext: CardContext? = null
    private var wholeData: Data? = null

    fun bindCardContext(cardContext: CardContext?) {
        this.cardContext = cardContext
    }

    fun resetDataList(newData: List<ListItemScopeData>) {
        scopeDataList.clear()
        scopeDataList.addAll(newData)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = scopeDataList.size

    override fun getItemViewType(position: Int): Int = scopeDataList[position].itemType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        try {
            val viewModel = itemTypeVMMap.get(viewType)

            return if (viewModel != null) {
                ListItemViewHolder(FrameLayout(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }, parent, viewType, viewModel, cardContext)
            } else {
                EmptyViewHolder(parent.context)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return EmptyViewHolder(parent.context)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        tryCatch {
            if (holder is ListItemViewHolder) {
                holder.bindData(wholeData, scopeDataList[position])
            }
        }
    }

    fun bindItemTypeVMMap(itemTypeVMMap: SparseArray<LayoutViewModel>) {
        this.itemTypeVMMap.clear()
        this.itemTypeVMMap.putAll(itemTypeVMMap)
    }

    fun bindWholeData(wholeData: Data?) {
        this.wholeData = wholeData
    }
}