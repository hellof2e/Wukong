package com.hellobike.magiccube.widget

import androidx.annotation.ColorInt

interface IBorder {
    fun setStrokeWidth(width: Float)
    fun setStrokeColor(@ColorInt color: Int)
    fun setRadius(radiusDp: Float)
    fun setDash(isDash: Boolean)
    fun setPressedStateChangedListener(listener: IPressedStateChangedListener?)
    fun setRadii(lt: Float, rt: Float, rb: Float, lb: Float)
}