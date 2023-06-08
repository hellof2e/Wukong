package com.hellobike.magiccube.v2.click

import android.view.View
import com.hellobike.magiccube.parser.DSLParser
import com.hellobike.magiccube.parser.widget.BaseWidget
import com.hellobike.magiccube.v2.configs.MagicInfo

internal class OnCardClickHandler(
    private var magicInfo: MagicInfo?
) : IOnCardClickListener {

    override fun onCardClick(baseWidget: BaseWidget<View>, url: String) {
        if (magicInfo?.onCubeClickListener != null) { // 新逻辑，需要由业务线判断是否拦截
            val intercept = magicInfo!!.onCubeClickListener!!.onCubeClick(url)

            if (!intercept) { // 不拦截，执行全局逻辑
                DSLParser.openWebBlock?.invoke(url)
            }
        } else {
            DSLParser.openWebBlock?.invoke(url) // 直接执行全局逻辑
        }
    }
}