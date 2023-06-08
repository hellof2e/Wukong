package com.hellobike.magiccube.v2.node

interface IVNodeContext {

    fun insertTraceShowNode(node: VNode)

    fun removeTraceShowNode(node: VNode)

    fun traceShow()
}