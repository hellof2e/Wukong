package com.hellobike.magiccube.v2.render

import android.content.Context
import com.hellobike.magiccube.parser.widget.IWidget
import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.node.VNode

interface IRender {

    fun injectMagicConfig(magicConfig: MagicConfig?): IRender

    fun injectCardContext(context: CardContext): IRender

    fun doRender(context: Context, vNode: VNode): IWidget
}