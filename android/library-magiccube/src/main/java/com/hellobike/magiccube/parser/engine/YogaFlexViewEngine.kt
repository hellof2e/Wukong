package com.hellobike.magiccube.parser.engine

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.facebook.yoga.*
import com.facebook.yoga.android.YogaLayout
import com.google.android.flexbox.*
import com.hellobike.magiccube.model.MagicUnit
import com.hellobike.magiccube.model.MagicValue
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.model.contractmodel.StyleViewModel
import com.hellobike.magiccube.model.contractmodel.configs.HGravity
import com.hellobike.magiccube.model.contractmodel.configs.MagicPosition
import com.hellobike.magiccube.parser.DSLParser.parseValue
import com.hellobike.magiccube.utils.safe2Float
import com.hellobike.magiccube.utils.safe2FloatWithNullable
import com.hellobike.magiccube.widget.BorderImageView
import com.hellobike.magiccube.widget.BorderTextView
import com.hellobike.magiccube.widget.MagicYogaLayout

class YogaFlexViewEngine : IViewEngine {

    private fun applyLayoutParams(view: View) {
//        if (view.layoutParams !is YogaLayout.LayoutParams) {
//            view.layoutParams = YogaLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//        }
        if (view.layoutParams == null) {
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun createContainer(context: Context): ViewGroup = MagicYogaLayout(context)

    override fun createImageView(context: Context): ImageView = BorderImageView(context)

    override fun createTextView(context: Context): TextView = BorderTextView(context)

    override fun createYogaNode(parent: ViewGroup, child: View): INodeAdapter {
        if (parent is MagicYogaLayout) {
            return INodeAdapter.YogaNodeAdapter(parent.createChildNode(child))
        }
        return INodeAdapter.YogaNodeAdapter(null)
    }

    override fun addChild(parent: ViewGroup, child: View, childNodeAdapter: INodeAdapter?) {
        if (parent is MagicYogaLayout && childNodeAdapter?.getYogaNode() != null) {
            parent.addYogaView(child, childNodeAdapter.getYogaNode()!!)
        } else {
            addChild(parent, child)
        }
    }

    override fun addChild(parent: ViewGroup, child: View, index: Int, childNodeAdapter: INodeAdapter?) {
        if (parent is MagicYogaLayout && childNodeAdapter?.getYogaNode() != null) {
            parent.addYogaView(child, childNodeAdapter.getYogaNode()!!, index)
        } else {
            addChild(parent, child, index)
        }
    }

    override fun addChild(parent: ViewGroup, child: View) {
        parent.addView(child)
    }

    override fun addChild(parent: ViewGroup, child: View, index: Int) {
        parent.addView(child, index)
    }

    override fun indexOfChild(parent: ViewGroup, child: View): Int {
        return parent.indexOfChild(child)
    }

    override fun applyLayout(view: View, layout: LayoutViewModel, nodeAdapter: INodeAdapter?) {
        applyLayoutParams(view)

        if (view is TextView) {
            view.gravity = (layout.textAlign ?: HGravity.START) or (Gravity.CENTER_VERTICAL)
        }

        applyWidthAndHeight(view, layout, nodeAdapter)
        applyFlexBox(layout, nodeAdapter)
        applyPadding(view, layout, nodeAdapter)
        applyMargin(layout, nodeAdapter)
        applyPosition(layout, nodeAdapter)
    }

    private fun applyPosition(layout: LayoutViewModel, nodeAdapter: INodeAdapter?) {
        if (MagicPosition.check(layout.position)) {
            nodeAdapter?.getYogaNode()?.positionType = YogaFlexConfigs.POSITION[layout.position]
        }

        val left = layout.left
        if (left != null) {
            val value = left.parseValue()

            when(value.unit) {
                MagicUnit.PIXEL -> nodeAdapter?.getYogaNode()?.setPosition(YogaEdge.LEFT, value.floatValue())
                MagicUnit.PERCENT -> nodeAdapter?.getYogaNode()?.setPositionPercent(YogaEdge.LEFT, value.floatValue())
            }
//            nodeAdapter?.getYogaNode()
//                ?.setPosition(YogaEdge.LEFT, layout.left!!.parseValue().floatValue())
        }

        val top = layout.top
        if (top != null) {
            val value = top.parseValue()

            when(value.unit) {
                MagicUnit.PIXEL -> nodeAdapter?.getYogaNode()?.setPosition(YogaEdge.TOP,  value.floatValue())
                MagicUnit.PERCENT -> nodeAdapter?.getYogaNode()?.setPositionPercent(YogaEdge.TOP,  value.floatValue())
            }

//            nodeAdapter?.getYogaNode()?.setPosition(YogaEdge.TOP, layout.top!!.parseValue().floatValue())
        }

        val right = layout.right
        if (right != null) {
            val value = right.parseValue()

            when(value.unit) {
                MagicUnit.PIXEL -> nodeAdapter?.getYogaNode()?.setPosition(YogaEdge.RIGHT, value.floatValue())
                MagicUnit.PERCENT -> nodeAdapter?.getYogaNode()?.setPositionPercent(YogaEdge.RIGHT, value.floatValue())
            }

//            nodeAdapter?.getYogaNode()
//                ?.setPosition(YogaEdge.RIGHT, layout.right!!.parseValue().floatValue())
        }

        val bottom = layout.bottom
        if (bottom != null) {
            val value = bottom.parseValue()

            when(value.unit) {
                MagicUnit.PIXEL -> nodeAdapter?.getYogaNode()?.setPosition(YogaEdge.BOTTOM, value.floatValue())
                MagicUnit.PERCENT -> nodeAdapter?.getYogaNode()?.setPositionPercent(YogaEdge.BOTTOM, value.floatValue())
            }

//            nodeAdapter?.getYogaNode()
//                ?.setPosition(YogaEdge.BOTTOM, layout.bottom!!.parseValue().floatValue())
        }
    }

    override fun applyStyle(view: View, style: StyleViewModel, nodeAdapter: INodeAdapter?) {
        if (style.borderWidth != null) {
            // 这边需要一个精度转换，否则会有一个像素的差距
            nodeAdapter?.getYogaNode()?.setBorder(YogaEdge.ALL, style.borderWidth!!.parseValue().intValue() * 1.0f)
        }
    }

    private fun applyWidthAndHeight(view: View, layout: LayoutViewModel, adapter: INodeAdapter?) {

        adapter?.getYogaNode()?.setWidthAuto()
        if (!layout.width.isNullOrBlank()) {
            val value = layout.width!!.parseValue()
            when (value.unit) {
                MagicUnit.PIXEL -> {
                    adapter?.getYogaNode()?.setWidth(value.intValue() * 1.0f)
                    view.layoutParams.width = value.intValue()
                }
                MagicUnit.PERCENT -> {
                    adapter?.getYogaNode()?.setWidthPercent(value.value)
                    if (value.value >= 100.0f) {
                        view.layoutParams?.width = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }
            }
        }


        if (!layout.aspectRatio.isNullOrBlank()) {
            val ratio = layout.aspectRatio!!.toFloatOrNull()
            if (ratio is Float) {
                adapter?.getYogaNode()?.aspectRatio = ratio
            }
        }

        if (layout.maxWidth != null) {
            val value = layout.maxWidth!!.parseValue()
            when(value.unit) {
                MagicUnit.PIXEL -> {
                    adapter?.getYogaNode()?.setMaxWidth(value.floatValue())
                }
                MagicUnit.PERCENT -> {
                    adapter?.getYogaNode()?.setMaxWidthPercent(value.value)
                }
            }
        }

        if (layout.minWidth != null) {
            val value = layout.minWidth!!.parseValue()
            when(value.unit) {
                MagicUnit.PIXEL -> {
                    adapter?.getYogaNode()?.setMinWidth(value.floatValue())
                }
                MagicUnit.PERCENT -> {
                    adapter?.getYogaNode()?.setMinWidthPercent(value.value)
                }
            }
        }


        adapter?.getYogaNode()?.setHeightAuto()
        if (!layout.height.isNullOrBlank()) {
            val value = layout.height!!.parseValue()
            when (value.unit) {
                MagicUnit.PIXEL -> {
                    adapter?.getYogaNode()?.setHeight(value.intValue() * 1.0f)
                    view.layoutParams.height = value.intValue()
                }
                MagicUnit.PERCENT -> {
                    adapter?.getYogaNode()?.setHeightPercent(value.value)
                    if (value.value >= 100.0f) {
                        view.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
                    }
                }
            }
        }

        if (layout.maxHeight != null) {
            val value = layout.maxHeight!!.parseValue()
            when(value.unit) {
                MagicUnit.PIXEL -> {
                    adapter?.getYogaNode()?.setMaxHeight(value.floatValue())
                }
                MagicUnit.PERCENT -> {
                    adapter?.getYogaNode()?.setMaxHeightPercent(value.value)
                }
            }
        }
        if (layout.minHeight != null) {
            val value = layout.minHeight!!.parseValue()
            when(value.unit) {
                MagicUnit.PIXEL -> {
                    adapter?.getYogaNode()?.setMinHeight(value.floatValue())
                }
                MagicUnit.PERCENT -> {
                    adapter?.getYogaNode()?.setMinHeightPercent(value.value)
                }
            }
        }
    }

    private fun applyFlexBox(layout: LayoutViewModel, adapter: INodeAdapter?) {
        val flexValue = layout.flex.safe2FloatWithNullable(1f, 0f)
        adapter?.getYogaNode()?.flexGrow = flexValue
        adapter?.getYogaNode()?.flexShrink = flexValue

        if (layout.flexGrow != null) {
            adapter?.getYogaNode()?.flexGrow = layout.flexGrow.safe2Float(0f)
        }

        if (layout.flexShrink != null) {
            adapter?.getYogaNode()?.flexShrink = layout.flexShrink.safe2Float(0f)
        }

        adapter?.getYogaNode()?.alignSelf = YogaFlexConfigs.ALIGN_SELF[layout.alignSelf
                ?: AlignSelf.AUTO] ?: YogaAlign.AUTO

        adapter?.getYogaNode()?.flexDirection = YogaFlexConfigs.FLEX_DIRECTION[layout.flexDirection
                ?: FlexDirection.ROW] ?: YogaFlexDirection.ROW
        adapter?.getYogaNode()?.wrap = YogaFlexConfigs.FLEX_WRAP[layout.flexWrap ?: FlexWrap.NOWRAP]
                ?: YogaWrap.NO_WRAP
        adapter?.getYogaNode()?.justifyContent = YogaFlexConfigs.JUSTIFY_CONTENT[layout.justifyContent
                ?: JustifyContent.FLEX_START] ?: YogaJustify.FLEX_START
        adapter?.getYogaNode()?.alignItems = YogaFlexConfigs.ALIGN_ITEMS[layout.alignItems
                ?: AlignItems.STRETCH] ?: YogaAlign.STRETCH
        adapter?.getYogaNode()?.alignContent = YogaFlexConfigs.ALIGN_CONTENT[layout.alignContent
                ?: AlignContent.STRETCH] ?: YogaAlign.STRETCH
    }

    private fun applyPadding(view: View, layout: LayoutViewModel, adapter: INodeAdapter?) {
        var paddingLeft = 0f
        var paddingRight = 0f
        var paddingTop = 0f
        var paddingBottom = 0f
        if (layout.paddingLeft != null) {
            paddingLeft = layout.paddingLeft!!.parseValue().floatValue()
        }
        if (layout.paddingRight != null) {
            paddingRight = layout.paddingRight!!.parseValue().floatValue()
        }
        if (layout.paddingTop != null) {
            paddingTop = layout.paddingTop!!.parseValue().floatValue()
        }
        if (layout.paddingBottom != null) {
            paddingBottom = layout.paddingBottom!!.parseValue().floatValue()
        }
        if (layout.paddingHorizontal != null) {
            paddingLeft = layout.paddingHorizontal!!.parseValue().floatValue()
            paddingRight = layout.paddingHorizontal!!.parseValue().floatValue()
        }
        if (layout.paddingVertical != null) {
            paddingTop = layout.paddingVertical!!.parseValue().floatValue()
            paddingBottom = layout.paddingVertical!!.parseValue().floatValue()
        }

        if (view is YogaLayout) {
            adapter?.getYogaNode()?.setPadding(YogaEdge.LEFT, paddingLeft)
            adapter?.getYogaNode()?.setPadding(YogaEdge.TOP, paddingTop)
            adapter?.getYogaNode()?.setPadding(YogaEdge.RIGHT, paddingRight)
            adapter?.getYogaNode()?.setPadding(YogaEdge.BOTTOM, paddingBottom)
        } else {
            view.setPadding(MagicValue.toInt(paddingLeft), MagicValue.toInt(paddingTop), MagicValue.toInt(paddingRight), MagicValue.toInt(paddingBottom))
        }
    }

    private fun applyMargin(layout: LayoutViewModel, adapter: INodeAdapter?) {
        var marginLeft = 0f
        var marginRight = 0f
        var marginTop = 0f
        var marginBottom = 0f
        if (layout.marginLeft != null) {
            marginLeft = layout.marginLeft!!.parseValue().floatValue()
        }
        if (layout.marginRight != null) {
            marginRight = layout.marginRight!!.parseValue().floatValue()
        }
        if (layout.marginTop != null) {
            marginTop = layout.marginTop!!.parseValue().floatValue()
        }
        if (layout.marginBottom != null) {
            marginBottom = layout.marginBottom!!.parseValue().floatValue()
        }
        if (layout.marginHorizontal != null) {
            marginLeft = layout.marginHorizontal!!.parseValue().floatValue()
            marginRight = layout.marginHorizontal!!.parseValue().floatValue()
        }
        if (layout.marginVertical != null) {
            marginTop = layout.marginVertical!!.parseValue().floatValue()
            marginBottom = layout.marginVertical!!.parseValue().floatValue()
        }


        adapter?.getYogaNode()?.setMargin(YogaEdge.LEFT, marginLeft)
        adapter?.getYogaNode()?.setMargin(YogaEdge.TOP, marginTop)
        adapter?.getYogaNode()?.setMargin(YogaEdge.RIGHT, marginRight)
        adapter?.getYogaNode()?.setMargin(YogaEdge.BOTTOM, marginBottom)
    }

}