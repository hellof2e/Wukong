package com.hellobike.magiccube.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView
import com.hellobike.magiccube.v2.ext.logt

open class SafeLottieAnimationView : LottieAnimationView {
    private var isErrorOccurs = false

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onDraw(canvas: Canvas?) {
        if (isErrorOccurs) return
        try {
            super.onDraw(canvas)
        } catch (e: Exception) {
            e.printStackTrace()
            logt(e)
            isErrorOccurs = true
        }
    }
}