package com.hellobike.magiccube.v2.node


class VNodeContext : IVNodeContext {

    private var traceShowVNodeList: HashSet<VNode>? = null

    override fun traceShow() {
        traceShowVNodeList?.forEach { it.traceShow() }
    }

    override fun insertTraceShowNode(node: VNode) {
        val set = traceShowVNodeList ?: HashSet()
        set.add(node)
        this.traceShowVNodeList = set
    }

    override fun removeTraceShowNode(node: VNode) {
        traceShowVNodeList?.remove(node)
    }
}