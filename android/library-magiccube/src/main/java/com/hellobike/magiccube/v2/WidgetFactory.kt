package com.hellobike.magiccube.v2

import android.content.Context
import com.hellobike.magiccube.parser.widget.*
import com.hellobike.magiccube.v2.configs.Constants
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.node.VNode

internal object WidgetFactory {

    fun createWidget(context: Context, vNode: VNode, cardContext: CardContext?, magicConfig: MagicConfig?): IWidget? {
        val widget = when (vNode.type) {
            "container", Constants.NODE_LIST_ITEM -> ContainerWidget(context)
            "img" -> ImageWidget(context)
            "text" -> TextWidget(context)
            "span" -> TextWidget(context)
            "lottie" -> LottieWidget(context)
            "m-for", "m-if" -> TagWidget(context)
            "progress" -> ProgressWidget(context)
            "counting" -> CountDownWidget(context)
            Constants.NODE_LIST_VIEW -> ListViewWidget(context)
            else -> {
                if (magicConfig != null) {
//                    return createByCustomer(context, vNode, magicConfig)
                    createByCustomer(context, vNode, magicConfig)
                } else null
            }
        }
        widget?.bindCardContext(cardContext)
        widget?.bindTemplate(vNode.template)
        return widget
    }

    // 自定义组件
    private fun createByCustomer(context: Context, vNode: VNode, config: MagicConfig): IWidget? {
        val factory = config.templateFactory

        return if (factory?.get(vNode.type) != null) {
            CustomerWidget(context, vNode.type, factory[vNode.type]!!)
        } else {
            null
        }
    }

}