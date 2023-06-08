package com.hellobike.magiccube.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ScaleDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import com.hellobike.magiccube.parser.engine.GradientParser
import com.hellobike.magiccube.utils.BorderHelper

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/9/16 8:11 PM
 */
class BorderProgressView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr), IBorder {

    private val hbBorderHelper: BorderHelper = BorderHelper(context, this)

    private val bgColor = GradientDrawable()
    private val pbColor = GradientDrawable()
    private var pressedStateChangedListener: IPressedStateChangedListener? = null

    override fun setPressedStateChangedListener(listener: IPressedStateChangedListener?) {
        this.pressedStateChangedListener = listener
    }

    init {
        bgColor.cornerRadius = 0.0f // 圆角
        bgColor.setColor(Color.parseColor("#888888"))

        pbColor.cornerRadius = 0.0f
        pbColor.setColor(Color.parseColor("#ffff00"))

        this.progress = 0
        this.max = 100

        // 这个进度会被裁切成矩形
//        val clip = ClipDrawable(pbColor, Gravity.START, ClipDrawable.HORIZONTAL)

        // 进度会是圆角
        val scale = ScaleDrawable(pbColor, Gravity.START, 1.0f, 0.0f)
        //待设置的Drawable数组
//        val progressDrawables = arrayOf(bgColor, clip)
        val progressDrawables = arrayOf(bgColor, scale)
        val layer = LayerDrawable(progressDrawables)

        layer.setId(0, android.R.id.background)
        layer.setId(1, android.R.id.progress)

        progressDrawable = layer
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun draw(canvas: Canvas?) {
        if (canvas != null) hbBorderHelper.draw(canvas)
        super.draw(canvas)
    }

    override fun setRadii(lt: Float, rt: Float, rb: Float, lb: Float) {
        hbBorderHelper.setRadii(lt, rt, rb, lb)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        canvas?.apply {
//            hbBorderHelper.dispatchRoundBorderDraw(this)
//        }
    }
    override fun setProgress(progress: Int) {
        super.setProgress(progress)
    }

    override fun setMax(max: Int) {
        super.setMax(max)
    }

    fun setProgressColor(@ColorInt color: Int) {
        pbColor.setColor(color)
        invalidate()
    }

    override fun setStrokeWidth(width: Float) {
//        hbBorderHelper.setBorderWidth(width.toInt())
    }

    override fun setStrokeColor(color: Int) {
//        hbBorderHelper.setBorderColor(color)
    }

    override fun setBackground(background: Drawable?) {
    }

    override fun setBackgroundColor(color: Int) {
//        super.setBackgroundColor(color)
        bgColor.setColor(color)
        invalidate()
    }

    fun setBackgroundGradient(gradientModel:GradientParser.GradientModel) {
        setGradient(gradientModel, bgColor)
    }

    fun setProgressGradient(gradientModel:GradientParser.GradientModel) {
        setGradient(gradientModel, pbColor)
    }

    private fun setGradient(model:GradientParser.GradientModel, gradient:GradientDrawable) {
        gradient.orientation = model.orientation
        gradient.gradientType = model.gradientType
        model.colors?.let { colorsIt ->
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
                && model.locations?.count() == colorsIt.count()) {
                gradient.setColors(colorsIt, model.locations)
            } else {
                gradient.colors = colorsIt
            }
        }
    }

    override fun setRadius(radiusDp: Float) {
        bgColor.cornerRadius = radiusDp
        pbColor.cornerRadius = radiusDp
//        hbBorderHelper.radius = radiusDp.toInt()
        invalidate()
    }

    override fun setDash(isDash: Boolean) {

    }

    override fun setPressed(pressed: Boolean) {
        super.setPressed(pressed)
        pressedStateChangedListener?.onPressedChanged(pressed)
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
    }

}