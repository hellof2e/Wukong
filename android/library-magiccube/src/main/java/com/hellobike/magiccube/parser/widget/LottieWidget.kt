package com.hellobike.magiccube.parser.widget

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.airbnb.lottie.LottieDrawable
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.model.contractmodel.LottieViewModel
import com.hellobike.magiccube.utils.LottieHelper
import com.hellobike.magiccube.utils.YogaUtils
import com.hellobike.magiccube.v2.template.Template
import com.hellobike.magiccube.widget.SafeLottieAnimationView

class LottieWidget(context: Context) : BaseWidget<SafeLottieAnimationView>(context) {
    override fun initView(): SafeLottieAnimationView = SafeLottieAnimationView(context).apply {
        repeatCount = LottieDrawable.INFINITE
    }


    override fun render(baseViewModel: BaseViewModel, template: Template) {
        super.render(baseViewModel, template)

        val lottie = baseViewModel as? LottieViewModel ?: return

        val src = template.getValue(lottie.src).stringValue()

        if (src.isNullOrBlank()) {
            getView().visibility = View.INVISIBLE
            return
        }
        getView().visibility = View.VISIBLE

        YogaUtils.updateAspectRatio(getNodeAdapter()?.getYogaNode(), src, lottie.layout)

        LottieHelper.loadLottie(context, src) { lottieCompose ->
            if (null == lottieCompose) return@loadLottie
            getView().setComposition(lottieCompose)
            getView().playAnimation()
        }

        val fit = template.getValue(lottie.fit).stringValue()
        if (!fit.isNullOrBlank()) {
            when (fit) {
                "fill" -> {
                    getView().scaleType = ImageView.ScaleType.FIT_XY
                }
                "contain" -> {
                    getView().scaleType = ImageView.ScaleType.CENTER_INSIDE
                }
                "cover" -> {
                    getView().scaleType = ImageView.ScaleType.CENTER_CROP
                }
                else -> {
                    getView().scaleType = ImageView.ScaleType.FIT_CENTER
                }
            }
        } else {
            getView().scaleType = ImageView.ScaleType.FIT_CENTER
        }

    }
}