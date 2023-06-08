package com.hellobike.magiccube.parser.widget

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.model.contractmodel.ImageViewModel
import com.hellobike.magiccube.parser.DSLParser
import com.hellobike.magiccube.utils.YogaUtils
import com.hellobike.magiccube.v2.template.Template
import com.hellobike.magiccube.widget.BorderImageView

class ImageWidget(context: Context) : BaseWidget<BorderImageView>(context) {

    override fun initView(): BorderImageView =
        viewEngine.createImageView(context) as BorderImageView


    override fun render(baseViewModel: BaseViewModel, template: Template) {
        super.render(baseViewModel, template)
        val imageViewModel = baseViewModel as? ImageViewModel ?: return


        val src = template.getValue(imageViewModel.src).stringValue()

        if (src.isNullOrBlank()) {
            getView().visibility = View.INVISIBLE
            return
        }
        getView().visibility = View.VISIBLE

        YogaUtils.updateAspectRatio(getNodeAdapter()?.getYogaNode(), src, baseViewModel.layout)

        if (!src.isNullOrBlank()) {
            if (context is Activity) {
                if (!DSLParser.isDestroy(context)) {
                    Glide.with(context).load(src).dontTransform().into(getView())
                }
            } else {
                Glide.with(context).load(src).dontTransform().into(getView())
            }
        }

        val fit = template.getValue(imageViewModel.fit).stringValue()
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
                "aspectFit" -> {
                    getView().scaleType = ImageView.ScaleType.FIT_CENTER
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