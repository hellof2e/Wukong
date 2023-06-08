package com.hellobike.magiccube.parser.spans

import android.text.TextPaint
import android.text.style.CharacterStyle

class LineThroughSpan : CharacterStyle() {

    override fun updateDrawState(tp: TextPaint?) {
        val textPaint = tp ?: return
        textPaint.flags = textPaint.flags or TextPaint.STRIKE_THRU_TEXT_FLAG
    }
}