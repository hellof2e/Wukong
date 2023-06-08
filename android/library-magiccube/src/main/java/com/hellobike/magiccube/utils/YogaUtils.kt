package com.hellobike.magiccube.utils

import android.net.Uri
import com.facebook.yoga.YogaNode
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel

object YogaUtils {


    fun updateAspectRatio(yogaNode: YogaNode?, src: String?, layout: LayoutViewModel?) {
        if (layout == null) return
        if (src.isNullOrBlank()) return
        if (yogaNode == null) return

        // 以自己配置的 aspectRatio 为准, 自己已经设置了，则不需要修改
        if (layout.aspectRatio.isNotNullOrBlank()) {
            return
        }

        if (layout.width.isNullOrBlank() || layout.height.isNullOrBlank()) {

            tryCatch {
                val uri = Uri.parse(src)

                val mcWidth = uri.getQueryParameter("mc_width")?.toIntOrNull() ?: 0
                val mcHeight = uri.getQueryParameter("mc_height")?.toIntOrNull() ?: 0

                if (mcWidth <= 0 || mcHeight <= 0) {
                    return@tryCatch
                }

                // 只有其中的width或者heigth没有设置任何数据的时候才检测
                yogaNode.aspectRatio = mcWidth * 1.0f / mcHeight
            }
        }
    }
}