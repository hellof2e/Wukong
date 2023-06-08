package com.hellobike.magiccube.parser.engine

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.model.contractmodel.StyleViewModel

interface IViewEngine {

    // 创建 Container
    fun createContainer(context: Context): ViewGroup

    // 创建 Image
    fun createImageView(context: Context): ImageView

    // 创建 text
    fun createTextView(context: Context): TextView

    // 创建 yoga node
    fun createYogaNode(parent: ViewGroup, child: View): INodeAdapter?

    // 添加子节点
    fun addChild(parent: ViewGroup, child: View, childNodeAdapter: INodeAdapter?)

    // 添加子节点
    fun addChild(parent: ViewGroup, child: View, index: Int, childNodeAdapter: INodeAdapter?)

    // 添加子节点
    fun addChild(parent: ViewGroup, child: View)

    // 添加子节点
    fun addChild(parent: ViewGroup, child: View, index: Int)

    // 查找子view的索引
    fun indexOfChild(parent: ViewGroup, child: View): Int

    // 应用 layout model
    fun applyLayout(view: View, layout: LayoutViewModel, nodeAdapter: INodeAdapter?)

    // 应用 style model
    fun applyStyle(view: View, style: StyleViewModel, nodeAdapter: INodeAdapter?)
}