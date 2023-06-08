package com.hellobike.magiccube.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import com.hellobike.magiccube.utils.BorderHelper

class CustomerWidgetContainer constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr), IWidgetAttachToWindow {

    private val hbBorderHelper: BorderHelper = BorderHelper(context, this)

    override fun dispatchDraw(canvas: Canvas?) {
        super.dispatchDraw(canvas)
        canvas ?: return
        hbBorderHelper.dispatchRoundBorderDraw(canvas)
    }

    override fun requestLayout() {
        super.requestLayout()
        if (getChildAt(0) is View) {
            (this.parent as? IParent)?.markDirty(this)
        }
    }

    fun setRadius(radiusDp: Float) {
        hbBorderHelper.setRadius(radiusDp.toInt())
    }


    fun setStrokeWidthColor(width: Float, radius: Float, color: Int) {
        hbBorderHelper.setBorderColor(color)
        hbBorderHelper.setBorderWidth(width.toInt())
    }

    fun setStrokeWidth(width: Float) {
        hbBorderHelper.setBorderWidth(width.toInt())
    }

    fun setStrokeColor(@ColorInt color: Int) {
        hbBorderHelper.setBorderColor(color)
    }

    private var onWidgetAttachToWindowChanged: IOnWidgetAttachToWindowChanged? = null

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
}