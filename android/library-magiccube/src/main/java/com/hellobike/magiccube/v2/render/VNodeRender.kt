package com.hellobike.magiccube.v2.render

import android.content.Context
import com.hellobike.magiccube.parser.engine.Engine
import com.hellobike.magiccube.parser.widget.ContainerWidget
import com.hellobike.magiccube.parser.widget.IWidget
import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.WidgetFactory
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.node.VForNode
import com.hellobike.magiccube.v2.node.VIfNode
import com.hellobike.magiccube.v2.node.VNode

class VNodeRender : IRender {

    private var magicConfig: MagicConfig? = null

    private var cardContext: CardContext? = null

    override fun injectMagicConfig(magicConfig: MagicConfig?): IRender {
        this.magicConfig = magicConfig
        return this
    }

    override fun injectCardContext(context: CardContext): IRender {
        this.cardContext = context
        return this
    }

    private fun handleWaitRemoveChildren(node: VNode, parentWidget: ContainerWidget?) {
        synchronized(node.waitRemoveChildren) {
            if (node.waitRemoveChildren.isNotEmpty()) { // 删除节点
                node.waitRemoveChildren.forEach { element ->
                    if (element.widget != null) {
                        parentWidget?.removeChild(element.widget!!)
                    }
                    if (element is VIfNode || element is VForNode) {
                        element.children.forEach {
                            if (it.widget != null) parentWidget?.removeChild(it.widget!!)
                        }
                    }
                }
            }
            node.clearRemoveWaitChildren()
        }
    }

    // 处理动态节点，比如 m-for , m-if 都属于动态节点，内部子节点会有很多种行为：删除，新增，更新等等....
    private fun renderDynamicVNode(context: Context, node: VNode, parentWidget: ContainerWidget?): IWidget? {
        // 仅仅作为占位处理
        val tagWidget = createOrGetWidget(context, node) ?: return null
        node.widget = tagWidget // VNode 关联 widget
        tagWidget.attachToParent(parentWidget)

        addChildToParent(parentWidget, tagWidget)

        // 处理待移除 VNode
        handleWaitRemoveChildren(node, parentWidget)

        if (node.children.isNotEmpty()) { // 更新或者创建节点
            node.children.forEachIndexed { _, vNode ->
                val tagIndex = parentWidget?.indexOfChild(tagWidget) ?: -1 // 获取占位所在的位置
                if (tagIndex >= 0) { // 新增的数据都要添加到占位所在的位置
                    val child = handleRender(context, vNode, parentWidget)

                    if (child != null && parentWidget != null && !parentWidget.containsV2(child)) {
                        child.removeViewFromParent()
                        parentWidget.addChild(child, tagIndex)
                    }
                }
            }
        }
        return tagWidget
    }


    private fun createOrGetWidget(context: Context, node: VNode): IWidget? {
        val widget = node.widget ?: WidgetFactory.createWidget(context, node, this.cardContext, this.magicConfig)
        widget?.bindCardContext(this.cardContext)
        widget?.bindTemplate(node.template)
        return widget
    }

    private fun renderElement(context: Context, node: VNode, parentWidget: ContainerWidget?): IWidget? {
        val widget = createOrGetWidget(context, node) ?: return null
        node.widget = widget // VNode 关联 widget
        widget.bindTemplate(node.template)

        widget.attachToParent(parentWidget)

        widget.doRender(node.baseViewModel, node.template)
        node.renderEnd()

        if (widget is ContainerWidget) {
            node.children.forEach {
                val child = handleRender(context, it, widget)
                addChildToParent(widget, child)
            }
        }
        return widget
    }

    private fun handleRender(context: Context, vNode: VNode, parentWidget: ContainerWidget?): IWidget? = when (vNode) {
        is VForNode,
        is VIfNode -> { // 遇到动态节点
            renderDynamicVNode(context, vNode, parentWidget)
        }
        else -> { // 渲染普通节点
            renderElement(context, vNode, parentWidget)
        }
    }

    private fun addChildToParent(parent: ContainerWidget?, child: IWidget?) {
        if (parent == null) return
        if (child == null) return

        if (!parent.containsV2(child)) {
            // 考虑到View的复用能力，需要先把 View 从之前的 ViewGroup 中移除
            child.removeViewFromParent()
            parent.addChild(child)
        }
    }


    // 渲染逻辑
    override fun doRender(context: Context, vNode: VNode): IWidget {

        // VNode 的 parent 是 null， 则需要先创建一个 parent
        val parent: ContainerWidget = if (vNode.widget?.getParentWidget() == null) {
            ContainerWidget(context).apply {
                useEngine(Engine.createEngineType(vNode.baseViewModel.engine))
            }
        } else {
            // 直接获取他本来的 parent
            vNode.widget!!.getParentWidget() as ContainerWidget
        }

        // render 的处理逻辑
        val widget = handleRender(context, vNode, parent)

        if (widget != null) {
            // 获取 当前 View 的 LayoutParams 赋值给 parent。主要是为了解决 Yoga 布局根节点无法自适应的问题
            if (vNode.isRootNode()) {
//                val lp = widget.getView().layoutParams
//                val width = lp.width
//                val height = lp.height
//                if (parent.getView().layoutParams != null) {
//
//                    val marginTop = widget.getNodeAdapter()?.getMarginTop() ?: 0f
//                    val marginBottom = widget.getNodeAdapter()?.getMarginBottom() ?: 0f
//                    val marginLeft = widget.getNodeAdapter()?.getMarginLeft() ?: 0f
//                    val marginRight = widget.getNodeAdapter()?.getMarginRight() ?: 0f
//
//                    if (width == ViewGroup.LayoutParams.MATCH_PARENT || width == ViewGroup.LayoutParams.WRAP_CONTENT) {
//                        parent.getView().layoutParams.width = width
//                    } else {
//                        parent.getView().layoutParams.width =
//                            width + marginLeft.toInt() + marginRight.toInt()
//                    }
//                    if (height == ViewGroup.LayoutParams.MATCH_PARENT || height == ViewGroup.LayoutParams.WRAP_CONTENT) {
//                        parent.getView().layoutParams.height = height
//                    } else {
//                        parent.getView().layoutParams.height =
//                            height + marginTop.toInt() + marginBottom.toInt()
//                    }
//                } else {
//                    val marginTop = widget.getNodeAdapter()?.getMarginTop() ?: 0f
//                    val marginBottom = widget.getNodeAdapter()?.getMarginBottom() ?: 0f
//                    val marginLeft = widget.getNodeAdapter()?.getMarginLeft() ?: 0f
//                    val marginRight = widget.getNodeAdapter()?.getMarginRight() ?: 0f
//                    val layoutParams = ViewGroup.LayoutParams(width, height)
//                    if (width == ViewGroup.LayoutParams.MATCH_PARENT || width == ViewGroup.LayoutParams.WRAP_CONTENT) {
//                        layoutParams.width = width
//                    } else {
//                        layoutParams.width = width + marginLeft.toInt() + marginRight.toInt()
//                    }
//                    if (height == ViewGroup.LayoutParams.MATCH_PARENT || height == ViewGroup.LayoutParams.WRAP_CONTENT) {
//                        layoutParams.height = height
//                    } else {
//                        layoutParams.height = height + marginTop.toInt() + marginBottom.toInt()
//                    }
//
//                    parent.getView().layoutParams = layoutParams
//                }

                parent.getView().clipChildren = vNode.baseViewModel.layout?.clipChildren ?: true
                parent.getView().clipToPadding = vNode.baseViewModel.layout?.clipChildren ?: true
            }

            addChildToParent(parent, widget)
        }
        return parent
    }

}