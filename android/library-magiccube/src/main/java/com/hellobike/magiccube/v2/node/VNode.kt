package com.hellobike.magiccube.v2.node

import android.graphics.Bitmap
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.parser.widget.IWidget
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.data.SafeMap
import com.hellobike.magiccube.v2.data.ScopeData
import com.hellobike.magiccube.v2.template.TemplateFactory
import kotlin.collections.ArrayList

/**
 * 虚拟节点
 */
open class VNode(val baseViewModel: BaseViewModel, val type: String) {

    var vNodeContext: IVNodeContext? = null

    var parent: VNode? = null // 关联 parent node

    var children: ArrayList<VNode> = ArrayList()

    var widget: IWidget? = null

    var wholeData: Data? = null // 全量数据

    var scopeData: ScopeData? = null // 自身的 listItem 数据

    var waitRemoveChildren: ArrayList<VNode> = ArrayList() // 等待移除的子节点

//    private var jsEngine: IJsEngine? = null

    val template = TemplateFactory.createTemplate(baseViewModel)

    private var cardContext: CardContext? = null

    private var waitTraceShowRunnable: Runnable? = null

    open fun detachFromParentNode() {
        children.forEach { it.detachFromParentNode() }
    }

    open fun onAttachedToWindow() {
        children.forEach { it.onAttachedToWindow() }
    }

    open fun onDetachedFromWindow() {
        children.forEach { it.onDetachedFromWindow() }
    }

    open fun onVisibilityChanged(isVisibility: Boolean) {
        children.forEach { it.onVisibilityChanged(isVisibility) }
    }

    fun traceShow() {
        if (widget == null) {
            val runnable = waitTraceShowRunnable ?: Runnable { widget?.traceShow(template, baseViewModel) }
            this.waitTraceShowRunnable = runnable
        } else {
            widget?.traceShow(template, baseViewModel)
        }
    }

    open fun renderEnd() {
        waitTraceShowRunnable?.run()
        waitTraceShowRunnable = null
    }

    fun isRootNode(): Boolean = parent == null

    // 关联js引擎
//    fun injectJsEngine(jsEngine: IJsEngine) {
//        this.jsEngine = jsEngine
//        template.bindJsEngine(jsEngine)
//    }

    fun injectCardContext(context: CardContext) {
        this.cardContext = context
        template.bindCardContext(context)
    }

    // 惯量全量数据
    fun bindWholeData(data: Data) {
        this.wholeData = data
        template.bindWholeData(data)
    }

    // 关联作用域数据
    fun bindScopeData(scopeData: ScopeData?) {
        this.scopeData = scopeData
        template.bindScopeData(scopeData)
    }

    // 关联赋节点
    fun bindParentNode(node: VNode?) {
        this.parent = node
        val context = node?.vNodeContext ?: VNodeContext()
        if (isRequiredTraceShow()) {
            context.insertTraceShowNode(this) // 记录需要埋点的节点
        }
        this.vNodeContext = context
        template.bindParentTemplate(this.parent?.template)
    }

    fun insertChild(vNode: VNode) {
        if (!children.contains(vNode)) {
            children.add(vNode)
        }
    }

    fun clearRemoveWaitChildren() {
        synchronized(waitRemoveChildren) {
            waitRemoveChildren.forEach {
                it.detachFromParentNode()
                if (it.isRequiredTraceShow()) { // 删除
                    this.vNodeContext?.removeTraceShowNode(it)
                }
            }
            waitRemoveChildren.clear()
        }
    }

    fun clearChildren() {
        synchronized(waitRemoveChildren) {
            waitRemoveChildren.addAll(children)
        }
        children.clear()
    }

    fun removeChildren(subList: List<VNode>) {
        synchronized(waitRemoveChildren) {
            waitRemoveChildren.addAll(subList)
        }
        children.removeAll(subList)
    }

    fun getValue(templateStr: String?): SafeMap = template.getValue(templateStr)

    open fun scanTemplate() = template.scan()

    // 是否需要记录曝光日志
    private fun isRequiredTraceShow(): Boolean = baseViewModel.action?.expose?.report != null

    fun findNodeById(nodeId: String?): VNode? {
        if (nodeId.isNullOrBlank()) return null
        if (nodeId == baseViewModel.nodeId) return this
        for (child in children) {
            val node =  child.findNodeById(nodeId)
            if (node != null) return node
        }
        return null
    }

    fun nodeToBitmap(): Bitmap? {
        val view = this.widget?.getView() ?: return null
        return UIUtils.viewToBitmap(view)
    }
}