package com.hellobike.magiccube.widget

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.text.DynamicLayout
import android.text.StaticLayout
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import com.hellobike.magiccube.model.contractmodel.SpanViewModel
import com.hellobike.magiccube.model.contractmodel.TextViewModel
import com.hellobike.magiccube.utils.BorderHelper
import java.lang.reflect.Field


/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/23 2:35 PM
 */
class BorderTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
)
    : AppCompatTextView(context, attrs, defStyleAttr), IBorder {
    private val hbBorderHelper: BorderHelper = BorderHelper(context, this)
    var textModel:TextViewModel? = null
    private var limitLines = Integer.MAX_VALUE

    var spanViewModel: SpanViewModel? = null
    private var pressedStateChangedListener: IPressedStateChangedListener? = null

    override fun setPressedStateChangedListener(listener: IPressedStateChangedListener?) {
        this.pressedStateChangedListener = listener
    }
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun draw(canvas: Canvas?) {
        if (canvas != null) hbBorderHelper.draw(canvas)
        super.draw(canvas)
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
    fun setLimitLines(lines:Int){
        limitLines = lines
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var layout: StaticLayout? = null
        var field: Field? = null
        try {
            val staticField: Field = DynamicLayout::class.java.getDeclaredField("sStaticLayout")
            staticField.setAccessible(true)
            layout = staticField.get(DynamicLayout::class.java) as StaticLayout?
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
        if (layout != null) {
            try {
                field = StaticLayout::class.java.getDeclaredField("mMaximumVisibleLineCount")
                field.setAccessible(true)
                field.setInt(layout, limitLines)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (layout != null && field != null) {
            try {
                field.setInt(layout, Int.MAX_VALUE)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        pressedStateChangedListener?.onPressedChanged(pressed)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
    }
}