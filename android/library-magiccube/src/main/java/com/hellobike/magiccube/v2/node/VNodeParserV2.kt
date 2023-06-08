package com.hellobike.magiccube.v2.node

import com.hellobike.magiccube.model.contractmodel.*
import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.configs.Constants
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.data.ScopeData
import com.hellobike.magiccube.v2.render.IRender
import com.hellobike.magiccube.v2.render.VNodeRender
import java.lang.IllegalStateException

class VNodeParserV2 : IVNodeEngine {

    companion object {
        fun create() = VNodeParserV2()
    }

    private var render: IRender = VNodeRender()
    private var cardContext: CardContext? = null

    override fun getRender(): IRender = render

    override fun injectCardContext(context: CardContext) {
        this.cardContext = context
    }

    private fun getCardContext(): CardContext {
        return cardContext ?: throw IllegalStateException("CardContext 没有初始化！请初始化 CardContext")
    }

    // ------------- diff 操作 ---------------

    @Throws(IllegalStateException::class)
    override fun diff(vNode: VNode, data: Data) {
        if (cardContext == null) {
            throw IllegalStateException("CardContext 没有初始化！请初始化 CardContext")
        }
        diffVNode(vNode, data, null)
    }

    @Throws(IllegalStateException::class)
    override fun diffWithScopeData(vNode: VNode, data: Data, scopeData: ScopeData) {
        if (cardContext == null) {
            throw IllegalStateException("CardContext 没有初始化！请初始化 CardContext")
        }
        diffVNode(vNode, data, scopeData)
    }

    private fun diffVNode(vNode: VNode, data: Data, scopeData: ScopeData?) {
        val viewModel = vNode.baseViewModel
        if (vNode is VIfNode) { // 遇到 if 节点
            diffVIfNode(viewModel, vNode, data, scopeData)
        } else if (vNode is VForNode) { // 遇到 for 节点
            diffVForNode(viewModel, vNode, data, scopeData)
        } else { // 更新数据
//            vNode.injectJsEngine(js())
            vNode.injectCardContext(getCardContext())
            vNode.bindWholeData(data)
            vNode.bindScopeData(scopeData)
            vNode.scanTemplate()
            if (viewModel is LayoutViewModel) {
                vNode.children.forEach {
                    diffVNode(it, data, scopeData)
                }
            }
        }
    }

    private fun diffVIfNode(viewModel: BaseViewModel, vNode: VNode, data: Data, scopeData: ScopeData?) {

        vNode.clearRemoveWaitChildren() // 首先清空待删除节点

//        vNode.injectJsEngine(js())
        vNode.injectCardContext(getCardContext())
        vNode.bindWholeData(data) // 绑定全局数据
        vNode.bindScopeData(scopeData) // 绑定域数据
        vNode.scanTemplate() // 扫描模版

        if (vNode.getValue(viewModel.mIf!!).boolValue() == true) { // if == true
            if (vNode.children.isEmpty()) { // 需要创建节点
                val child = parseOtherVNode(viewModel, data, vNode, scopeData)
                vNode.insertChild(child)
            } else { // 复用节点
                diffVNode(vNode.children[0], data, scopeData)
            }
        } else { // 需要删除节点
            vNode.clearChildren()
        }
    }

    private fun diffVForNode(viewModel: BaseViewModel, vNode: VNode, data: Data, scopeData: ScopeData?) {
        vNode.clearRemoveWaitChildren() // 首先清除所有待删除节点

//        vNode.injectJsEngine(js())
        vNode.injectCardContext(getCardContext())
        vNode.bindWholeData(data) // 绑定全局数据
        vNode.bindScopeData(scopeData) // for节点需要先从parent中解析出list数据，才能作为自己的域数据
        vNode.scanTemplate()

        val value = vNode.getValue(viewModel.mFor!!) //获取新的数据

        // 遍历创建 listItem 节点，并添加到 for 节点中
        value.listValue()?.forEachIndexed { index, item ->
            val newScopeData = ScopeData(item, index)
            newScopeData.bindScopePrefixName(viewModel.scopePrefixName, viewModel.scopeIndexPrefixName)

            if (index >= vNode.children.size) { // 需要创建新的节点
                val childNode = parseVNode(viewModel, data, vNode, newScopeData)
                vNode.insertChild(childNode)
            } else { // 直接复用节点
                val child = vNode.children[index]
                diffVNode(child, data, newScopeData)
            }
        }

        if (value.listValue().isNullOrEmpty()) { // 如果数据是空，则需要删除所有节点
            vNode.clearChildren()
        } else if (value.listValue()!!.size < vNode.children.size) { // 删除多余的节点
            val subList = vNode.children.subList(value.listValue()!!.size, vNode.children.size)
            vNode.removeChildren(subList)
        }
    }


    // ---------------- 创建操作 -------------------

    @Throws(IllegalStateException::class)
    override fun parse(viewModel: LayoutViewModel, data: Data): VNode {
        if (cardContext == null) {
            throw IllegalStateException("CardContext 没有初始化！请初始化 CardContext")
        }
        return parseVNode(viewModel, data, null, null)
    }

    @Throws(IllegalStateException::class)
    override fun parseWithScopeData(
        viewModel: LayoutViewModel,
        data: Data,
        scopeData: ScopeData
    ): VNode {
        if (cardContext == null) {
            throw IllegalStateException("CardContext 没有初始化！请初始化 CardContext")
        }
        return parseVNode(viewModel, data, null, scopeData)
    }

    private fun parseVNode(viewModel: BaseViewModel, data: Data, parentNode: VNode?, scopeData: ScopeData?): VNode {

        if (viewModel.hasIf()) { // 包含 if 能力，则需要首先创建一个虚拟的 if 节点
            val ifNode = createVIfNode(viewModel, data, scopeData, parentNode)
            // if 表达式执行成立，可以创建这个节点，创建完成之后，需要关联到 if 节点上
            if (ifNode.getValue(viewModel.mIf!!).boolValue() == true) {
                val child = parseOtherVNode(viewModel, data, ifNode, scopeData)
                ifNode.insertChild(child) // 将创建的节点关联到 if 节点上
            }
            return ifNode // 返回 if 节点
        } else { // 不是 if 节点，则 创建普通节点
            return parseOtherVNode(viewModel, data, parentNode, scopeData)
        }
    }


    private fun parseOtherVNode(viewModel: BaseViewModel, data: Data, parentNode: VNode?, scopeData: ScopeData?): VNode {
        val vNode = if (viewModel.type == Constants.NODE_COUNTING) {
            createVCountDownNode(viewModel, data, scopeData, parentNode)
        } else if (viewModel.type == Constants.NODE_LIST_VIEW) {
            createVListViewNode(viewModel, data, scopeData, parentNode)
        } else {
            createVNode(viewModel, data, scopeData, parentNode)
        }

        if (viewModel is LayoutViewModel) {
            if (vNode is VListViewNode) {
                // do nothing
            } else {
                // 如果创建的是 container， 还需要处理children
                parseContainer(viewModel, data, vNode, scopeData)
            }
        }
        return vNode
    }

    private fun parseContainer(
        viewModel: LayoutViewModel,
        data: Data,
        parentNode: VNode,
        scopeData: ScopeData?
    ) {
        // 处理 container 的 children
        viewModel.childList?.forEach { child ->
            if (child.hasFor()) {
                // 处理 child for 逻辑
                handleForChildren(child, data, parentNode, scopeData)
            } else {
                // 处理 child 非 for 逻辑
                handleChild(child, data, parentNode, scopeData)
            }
        }
    }

    private fun handleForChildren(viewModel: BaseViewModel, data: Data, parentNode: VNode, scopeData: ScopeData?) {

        val forNode = createVForNode(viewModel, data, parentNode, scopeData)
        val list = forNode.getValue(viewModel.mFor!!).listValue()

        // 遍历创建节点，并添加到 for 节点中
        list?.forEachIndexed { index, item ->
            val newScopeData = ScopeData(item, index)
            newScopeData.bindScopePrefixName(viewModel.scopePrefixName, viewModel.scopeIndexPrefixName)
            // 遍历的过程中会产生 scope 数据信息，以及index信息
            val childNode = parseVNode(viewModel, data, forNode, newScopeData)
            forNode.insertChild(childNode)
        }

        // 将 for 节点添加到 container 中
        parentNode.insertChild(forNode)
    }

    private fun handleChild(viewModel: BaseViewModel, data: Data, parentNode: VNode, scopeData: ScopeData?) {
        val childNode = parseVNode(viewModel, data, parentNode, scopeData)
        parentNode.insertChild(childNode)
    }

    private fun createVCountDownNode(viewModel: BaseViewModel, data: Data, scopeData: ScopeData?, parentNode: VNode?): VNode {
        val node = VCountDownNode(viewModel, viewModel.type!!)
        initVNode(node, data, parentNode, scopeData)
        node.setOnCountDownCallback(object : VCountDownNode.ICountDownCallback {
            override fun reDiff(node: VNode, data: Data, scopeData: ScopeData?) {
                val widget = node.widget ?: return
                diffVNode(node, data, scopeData) // 重新 diff
                render.doRender(widget.getView().context, node) // 重新 render
            }
        })
        return node
    }

    private fun createVListViewNode(
        viewModel: BaseViewModel,
        data: Data,
        scopeData: ScopeData?,
        parentNode: VNode?
    ): VNode {
        val node = VListViewNode(viewModel, viewModel.type!!)
        initVNode(node, data, parentNode, scopeData)
        return node
    }

    private fun createVNode(
        viewModel: BaseViewModel,
        data: Data,
        scopeData: ScopeData?,
        parentNode: VNode?
    ): VNode {
        val node = VNode(viewModel, viewModel.type!!)
        initVNode(node, data, parentNode, scopeData)
        return node
    }

    private fun createVIfNode(
        viewModel: BaseViewModel,
        data: Data,
        scopeData: ScopeData?,
        parentNode: VNode?
    ): VNode {
        val ifNode = VIfNode(viewModel, Constants.NODE_TYPE_IF)
        initVNode(ifNode, data, parentNode, scopeData)
        return ifNode
    }

    private fun createVForNode(
        viewModel: BaseViewModel,
        data: Data,
        parentNode: VNode?,
        scopeData: ScopeData?
    ): VNode {
        // 首先需要创建一个 for 虚拟节点，for 节点需要首先解析出list数据才能遍历，而且list数据需要从父作用域中查找
        val forNode = VForNode(viewModel, Constants.NODE_TYPE_FOR)
        initVNode(forNode, data, parentNode, scopeData)
        return forNode
    }

    private fun initVNode(vNode: VNode, data: Data, parentNode: VNode?, scopeData: ScopeData?) {
//        vNode.injectJsEngine(js())
        vNode.injectCardContext(getCardContext())
        vNode.bindWholeData(data) // 绑定全局数据
        vNode.bindScopeData(scopeData) // 绑定作用域数据
        vNode.bindParentNode(parentNode) // 绑定父节点
        vNode.scanTemplate() // 创建 state
    }
}

