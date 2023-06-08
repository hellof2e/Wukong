package com.hellobike.magiccube.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import com.hellobike.magiccube.utils.BorderHelper

class BorderRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), IBorder, IWidgetAttachToWindow {

    private val hbBorderHelper: BorderHelper = BorderHelper(context, this)
    private var pressedStateChangedListener: IPressedStateChangedListener? = null

    override fun setPressedStateChangedListener(listener: IPressedStateChangedListener?) {
        this.pressedStateChangedListener = listener
    }
    override fun draw(c: Canvas?) {
        if (c != null) hbBorderHelper.draw(c)
        super.draw(c)
    }

    override fun setRadii(lt: Float, rt: Float, rb: Float, lb: Float) {
        hbBorderHelper.setRadii(lt, rt, rb, lb)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.apply {
            hbBorderHelper.dispatchRoundBorderDraw(this)
        }
    }

    override fun setRadius(radiusDp: Float) {
        hbBorderHelper.setRadius(radiusDp.toInt())
    }


    fun setStrokeWidthColor(width: Float, radius: Float, color: Int) {
        hbBorderHelper.setBorderColor( color)
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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onWidgetAttachToWindowChanged?.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onWidgetAttachToWindowChanged?.onDetachedFromWindow()
    }

    private var onWidgetAttachToWindowChanged: IOnWidgetAttachToWindowChanged? = null

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