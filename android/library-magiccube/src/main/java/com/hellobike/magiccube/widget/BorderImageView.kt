package com.hellobike.magiccube.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import com.hellobike.magiccube.utils.BorderHelper

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/9/16 8:11 PM
 */
class BorderImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
)
    : AppCompatImageView(context, attrs, defStyleAttr), IBorder {
    private val hbBorderHelper: BorderHelper = BorderHelper(context, this)

    private var pressedStateChangedListener: IPressedStateChangedListener? = null

    override fun setPressedStateChangedListener(listener: IPressedStateChangedListener?) {
        this.pressedStateChangedListener = listener
    }
    override fun setRadii(lt: Float, rt: Float, rb: Float, lb: Float) {
        hbBorderHelper.setRadii(lt, rt, rb, lb)
    }

    override fun draw(canvas: Canvas?) {
        if (canvas != null) hbBorderHelper.draw(canvas)
        super.draw(canvas)
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

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        pressedStateChangedListener?.onPressedChanged(pressed)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
    }
}