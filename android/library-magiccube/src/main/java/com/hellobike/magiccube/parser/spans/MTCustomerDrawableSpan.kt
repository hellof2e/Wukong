package com.hellobike.magiccube.parser.spans

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.LineHeightSpan
import android.text.style.ReplacementSpan
import android.text.style.UpdateLayout
import com.hellobike.magiccube.v2.ext.logd

class MTCustomerDrawableSpan(private val drawable: Drawable) :
    ReplacementSpan(), LineHeightSpan, UpdateLayout {

//    private val paintTemp: Paint by lazy {
//        Paint().apply {
//            color = Color.RED
//        }
//    }

    companion object {
        fun createStrokeDrawableSpan(
            strokeWidth: Int,
            stokeColor: Int,
            ltR: Float,
            rtR: Float,
            rbR: Float,
            lbR: Float,
        ): MTCustomerDrawableSpan {
            val drawable = GradientDrawable()
            drawable.setStroke(strokeWidth, stokeColor)
            drawable.cornerRadii = floatArrayOf(ltR, ltR, rtR, rtR, rbR, rbR, lbR, lbR)
            return MTCustomerDrawableSpan(drawable)
        }

        fun createGradientDrawableSpan(
            colors: IntArray,
            orientation: GradientDrawable.Orientation,
            ltR: Float,
            rtR: Float,
            rbR: Float,
            lbR: Float,
        ): MTCustomerDrawableSpan {
            val drawable = GradientDrawable()
            drawable.cornerRadii = floatArrayOf(ltR, ltR, rtR, rtR, rbR, rbR, lbR, lbR)
            drawable.colors = colors
            drawable.orientation = orientation
            return MTCustomerDrawableSpan(drawable)
        }
    }


    private val fontMetrics: Paint.FontMetricsInt by lazy { Paint.FontMetricsInt() }
    private var pl = 0
    private var pt = 0
    private var pr = 0
    private var pb = 0
    private var marginStart = 0
    private var marginEnd = 0

    override fun getSize(
        paint: Paint,
        text: CharSequence?,
        start: Int,
        end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val content = text ?: ""

        val textWidth = paint.measureText(content, start, end)

        if (fm == null) {
            return (textWidth + pl + pr + marginStart + marginEnd).toInt()
        }

        val textHeight = fm.descent - fm.ascent

        drawable.setBounds(0, 0, (textWidth + pl + pr).toInt(), textHeight + pt + pb)
        return drawable.bounds.width() + marginStart + marginEnd
    }


    private var combSpans: Array<CharacterStyle>? = null


    fun combSpan(span: Array<CharacterStyle>) {
        this.combSpans = span
    }


    fun setPadding(l: Int, t: Int, r: Int, b: Int) {
        this.pl = l
        this.pt = t
        this.pr = r
        this.pb = b
    }

    fun setMargin(marginStart: Int, marginEnd: Int) {
        this.marginStart = marginStart
        this.marginEnd = marginEnd
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence?,
        start: Int,
        end: Int,
        x: Float, // baseline
        top: Int,
        y: Int, // baseline
        bottom: Int,
        paint: Paint
    ) {

        val startTime = System.currentTimeMillis()
        val content = text ?: ""

        canvas.save()

        paint.getFontMetricsInt(fontMetrics)

        val height = bottom - top // 好像是 descent 和 ascent 之间的高度

//        val textWidth = paint.measureText(text, start, end)
        val textHeight = fontMetrics.descent - fontMetrics.ascent


        val centerY = height / 2.0f

        // 测试绘制中心线
//        canvas.drawLine(
//            x,
//            centerY,
//            x + textWidth + marginStart + marginEnd + pr + pl,
//            centerY,
//            paintTemp
//        )

        // 绘制 drawable
        canvas.translate(x + marginStart.toFloat(), centerY - drawable.bounds.height() / 2)
        drawable.draw(canvas)

        canvas.restore()

        if (paint is TextPaint) {
            combSpans?.forEach {
                if (it !is MTCustomerDrawableSpan) {
                    it.updateDrawState(paint)
                }
            }
        }

        canvas.drawText(
            content,
            start,
            end,
            x + marginStart + pl,
            centerY + textHeight / 2 - fontMetrics.descent,
            paint
        )

        logd("duration: ${System.currentTimeMillis() - startTime}")
    }


    private var isInitOriginArgs = false
    private var originAscent = -1
    private var originDescent = -1
    private var originTop = -1
    private var originBottom = -1

    override fun chooseHeight(
        text: CharSequence?,
        start: Int,
        end: Int,
        spanstartv: Int,
        lineHeight: Int,
        fm: Paint.FontMetricsInt
    ) {
        val spanned = text as? Spanned ?: return

        val spanStart = spanned.getSpanStart(this)
        val spanEnd = spanned.getSpanEnd(this)


        if (!isInitOriginArgs) {
            originTop = fm.top
            originAscent = fm.ascent
            originDescent = fm.descent
            originBottom = fm.bottom
            isInitOriginArgs = true
        }

        // 计算行高
        if (spanStart in start..end && spanEnd in start..end && spanEnd > spanStart && (pt > 0 || pb > 0)) {
            fm.ascent -= pt
            fm.top -= pt
            fm.descent += pb
            fm.bottom += pb
        } else {
            // 恢复行距
//            fm.ascent = originAscent
//            fm.top = originTop
//            fm.descent = originDescent
//            fm.bottom = originBottom
        }
    }
}