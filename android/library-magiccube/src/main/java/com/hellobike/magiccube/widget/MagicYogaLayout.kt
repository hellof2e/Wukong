package com.hellobike.magiccube.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.viewpager.widget.ViewPager
import com.facebook.yoga.YogaDisplay
import com.facebook.yoga.YogaNode
import com.facebook.yoga.YogaNodeFactory
import com.facebook.yoga.android.YogaLayout
import com.hellobike.magiccube.utils.BorderHelper
import com.hellobike.magiccube.v2.ext.logd

open class MagicYogaLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : YogaLayout(context, attrs, defStyleAttr), IBorder, IParent, IWidgetAttachToWindow {
    private var hasVisible = false

    private val hbBorderHelper: BorderHelper = BorderHelper(context, this)
    private var pressedStateChangedListener: IPressedStateChangedListener? = null

    override fun setPressedStateChangedListener(listener: IPressedStateChangedListener?) {
        this.pressedStateChangedListener = listener
    }
    private val mYogaNodesField by lazy {
        YogaLayout::class.java.getDeclaredField("mYogaNodes").apply {
            isAccessible = true
        }
    }
    private val mMapPutMethod by lazy {
        Map::class.java.getMethod("put", Any::class.java, Any::class.java).apply {
            isAccessible = true
        }
    }

    init {
        hasVisible = visibility == View.VISIBLE
    }

    override fun markDirty(view: View) {
        val yogaNodeForView = getYogaNodeForView(view)
        if (yogaNodeForView != null) {
            if (!yogaNodeForView.isMeasureDefined) {
                yogaNodeForView.setMeasureFunction(ViewMeasureFunction())
            }
            invalidate(view)
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)

        if (child?.visibility == GONE) {
            getYogaNodeForView(child)?.display = YogaDisplay.NONE
        }

    }
    /**
     * 创建 child 节点
     */
    public fun createChildNode(child: View): YogaNode {
        if (child.layoutParams !is LayoutParams) {
            child.layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        val childNode = if (child is YogaLayout) {
            child.yogaNode
        } else {
            val node = getYogaNodeForView(child) ?: YogaNodeFactory.create()
            node.data = child
            node
        }

        applyLayoutParams(child.layoutParams as LayoutParams, childNode, child)
        // 必须在这里设置，否则需要选用第二种方案，就是调用 applyYoga 方法
        // 注意:: Yoga 的 C++ 层的实现，在调用 node.setMeasureFunction 的时候，当前node节点的 children_.size() 必须是0
        //  YGAssertWithNode(
        //        this,
        //        children_.size() == 0,
        //        "Cannot set measure function: Nodes with measure functions cannot have "
        //        "children.");
        childNode.setMeasureFunction(ViewMeasureFunction())
        return childNode
    }


    /**
     * 添加child节点
     */
    public fun addYogaView(child: View, childNode: YogaNode) {
        addView(child, childNode)
        yogaNode.addChildAt(childNode, yogaNode.childCount)
    }

    public fun addYogaView(child: View, childNode: YogaNode, index: Int) {
        try {
            val mYogaNodes = mYogaNodesField.get(this)
            mMapPutMethod.invoke(mYogaNodes, child, childNode)

            addView(child, index)

            if (index >= yogaNode.childCount) {
                yogaNode.addChildAt(childNode, yogaNode.childCount)
            } else {
                yogaNode.addChildAt(childNode, index)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    override fun setRadii(lt: Float, rt: Float, rb: Float, lb: Float) {
        hbBorderHelper.setRadii(lt, rt, rb, lb)
    }

    override fun draw(canvas: Canvas?) {
        if (canvas != null) hbBorderHelper.draw(canvas)
        super.draw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas?.apply {
            hbBorderHelper.dispatchRoundBorderDraw(this)
        }
    }

    override fun setRadius(radiusDp: Float) {
        hbBorderHelper.setRadius(radiusDp.toInt())
    }

    fun setStrokeWidthColor(width: Float, radius: Float, color: Int) {
        hbBorderHelper.setBorderColor(color)
        hbBorderHelper.setBorderWidth(width.toInt())
    }

    override fun setDash(isDash: Boolean) {
//        hbBorderHelper.set = isDash
    }

    override fun setStrokeWidth(width: Float) {
        hbBorderHelper.setBorderWidth(width.toInt())
    }

    override fun setStrokeColor(@ColorInt color: Int) {
        hbBorderHelper.setBorderColor(color)
    }

//    private var otherWidth: Float = -1.0f
//    private var otherHeight: Float = -1.0f

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (parent !is YogaLayout && childCount == 1 && yogaNode.childCount == 1 && getChildAt(0) is YogaLayout) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec) // 首先测量一次，计算出子view的大小

            // 当子View发生变化之后，自己根据子view的大小计算自己的宽高
            val child = getChildAt(0)
            if (measuredWidth > 0 && measuredHeight > 0 && child.measuredWidth > 0 && child.measuredHeight > 0) {
                val otherWidth = (measuredWidth - child.measuredWidth).toFloat()
                val otherHeight = (measuredHeight - child.measuredHeight).toFloat()
                val width = otherWidth + yogaNode.getChildAt(0).layoutWidth
                val height = otherHeight + yogaNode.getChildAt(0).layoutHeight
                setMeasuredDimension(resolveSize(width.toInt(), widthMeasureSpec), resolveSize(height.toInt(), heightMeasureSpec))
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }

    }

    private var onWidgetAttachToWindowChanged: IOnWidgetAttachToWindowChanged? = null

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)
        if (VISIBLE == visibility && getLocalVisibleRect(Rect())) { // 可见
            hasVisible = true
            onWidgetAttachToWindowChanged?.onVisibilityChanged(true)
        } else if (GONE == visibility || INVISIBLE == visibility) { // 不可见
            hasVisible = false
            onWidgetAttachToWindowChanged?.onVisibilityChanged(false)
        }
    }

    override fun onVisibilityAggregated(isVisible: Boolean) {
        super.onVisibilityAggregated(isVisible)
        if (hasVisible != isVisible) {
            hasVisible = isVisible
            onWidgetAttachToWindowChanged?.onVisibilityChanged(isVisible)
        }
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onWidgetAttachToWindowChanged?.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onWidgetAttachToWindowChanged?.onDetachedFromWindow()
    }

    override fun setWidgetAttachToWindowChanged(listener: IOnWidgetAttachToWindowChanged) {
        this.onWidgetAttachToWindowChanged = listener
    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        pressedStateChangedListener?.onPressedChanged(pressed)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
    }
}