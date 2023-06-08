package com.hellobike.magiccube.parser.engine

import com.facebook.yoga.YogaEdge
import com.facebook.yoga.YogaNode

/**
 * 对于 Node 具体实现的包装
 */
interface INodeAdapter {

    fun getYogaNode(): YogaNode?

    fun getMarginLeft(): Float

    fun getMarginTop(): Float

    fun getMarginRight(): Float

    fun getMarginBottom(): Float

    class YogaNodeAdapter(private val node: YogaNode?) : INodeAdapter {

        override fun getYogaNode(): YogaNode? = node

        override fun getMarginLeft(): Float = node?.getMargin(YogaEdge.LEFT)?.value ?: 0f

        override fun getMarginTop(): Float = node?.getMargin(YogaEdge.TOP)?.value ?: 0f

        override fun getMarginRight(): Float = node?.getMargin(YogaEdge.RIGHT)?.value ?: 0f

        override fun getMarginBottom(): Float = node?.getMargin(YogaEdge.BOTTOM)?.value ?: 0f
    }

}