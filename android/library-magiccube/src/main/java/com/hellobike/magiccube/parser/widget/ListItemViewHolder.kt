package com.hellobike.magiccube.parser.widget

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.data.ListItemScopeData
import com.hellobike.magiccube.v2.node.VNode
import com.hellobike.magiccube.widget.IOnWidgetAttachToWindowChanged
import com.hellobike.magiccube.widget.IWidgetAttachToWindow

internal class ListItemViewHolder(
    private val viewGroup: ViewGroup,
    private val parent: ViewGroup,
    private val viewType: Int,
    private val viewModel: LayoutViewModel,
    private val cardContext: CardContext?
) : RecyclerView.ViewHolder(viewGroup), IOnWidgetAttachToWindowChanged {

    private var vNodeTree: VNode? = null

    private fun reset() {
        (itemView as? ViewGroup)?.removeAllViews()
        vNodeTree?.detachFromParentNode()
        vNodeTree = null
    }

    fun bindData(wholeData: Data?, scopeData: ListItemScopeData) {
        val cardContext = cardContext
        if (cardContext == null || wholeData == null) {
            reset()
            return
        }

        if (scopeData.itemType != viewType) {
            reset()
        }

        val vNode = vNodeTree
        if (vNode == null) { // 初次创建节点
            createVNodeTree(cardContext, viewModel, wholeData, scopeData)
        } else { // diff
            updateVNodeTree(cardContext, vNode, wholeData, scopeData)
        }
    }

    private fun createVNodeTree(
        cardContext: CardContext,
        viewModel: LayoutViewModel,
        wholeData: Data,
        scopeData: ListItemScopeData
    ) {
        val vNodeTree = try {
            cardContext.vNodeEngine?.parseWithScopeData(viewModel, wholeData, scopeData)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        if (vNodeTree !is VNode) return

        val widget = try {
            cardContext.vNodeEngine?.getRender()?.doRender(itemView.context, vNodeTree)
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        if (widget !is IWidget) return


        val view = widget.getView()

        if (view is IWidgetAttachToWindow) {
            view.setWidgetAttachToWindowChanged(this)
        }

        viewGroup.removeAllViews()
        viewGroup.addView(view)
        this.vNodeTree = vNodeTree
    }

    private fun updateVNodeTree(
        cardContext: CardContext,
        vNode: VNode,
        wholeData: Data,
        scopeData: ListItemScopeData
    ) {
        val diffSuccess = try {
            cardContext.vNodeEngine?.diffWithScopeData(vNode, wholeData, scopeData)
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            false
        }

        if (!diffSuccess) return

        try {
            cardContext.vNodeEngine?.getRender()?.doRender(itemView.context, vNode)
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun onAttachedToWindow() {
        vNodeTree?.onAttachedToWindow()
        vNodeTree?.vNodeContext?.traceShow()
    }

    override fun onDetachedFromWindow() {
        vNodeTree?.onDetachedFromWindow()
    }

    override fun onVisibilityChanged(isVisibility: Boolean) {
        vNodeTree?.onVisibilityChanged(isVisibility)
    }
}