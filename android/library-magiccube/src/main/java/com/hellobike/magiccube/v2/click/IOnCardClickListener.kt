package com.hellobike.magiccube.v2.click

import android.view.View
import com.hellobike.magiccube.parser.widget.BaseWidget

interface IOnCardClickListener {
    /**
     * 卡片点击
     */
    fun onCardClick(baseWidget: BaseWidget<View>, url: String)
}