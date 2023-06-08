package com.hellobike.magiccube.parser.widget

import android.content.Context
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.model.contractmodel.ProgressViewModel
import com.hellobike.magiccube.parser.engine.ColorParser
import com.hellobike.magiccube.parser.engine.GradientParser
import com.hellobike.magiccube.v2.template.Template
import com.hellobike.magiccube.widget.BorderProgressView

class ProgressWidget(context: Context) : BaseWidget<BorderProgressView>(context) {

    override fun initView(): BorderProgressView = BorderProgressView(context)


    override fun render(baseViewModel: BaseViewModel, template: Template) {
        super.render(baseViewModel, template)


        val progressViewModel = baseViewModel as? ProgressViewModel ?: return

        val maxProgress = template.getValue(progressViewModel.maxProgress).intValue()
        if (maxProgress != null) getView().max = maxProgress

        val progress = template.getValue(progressViewModel.progress).intValue()
        if (progress != null) getView().progress = progress

        val background = baseViewModel.style?.background
        if (!background.isNullOrBlank()) {
            val colorStr = template.getValue(background).stringValue()
            if (!colorStr.isNullOrBlank()) {
                val gradientModel = GradientParser.parseGradient(colorStr)
                if (gradientModel != null) {
                    getView().setBackgroundGradient(gradientModel)
                }
            }
        }

        val progressColor = template.getValue(progressViewModel.progressColor).stringValue()
        if (!progressColor.isNullOrBlank()) {
            val gradient = GradientParser.parseGradient(progressColor)
            if (gradient != null) {
                getView().setProgressGradient(gradient)
            } else {
                getView().setProgressColor(ColorParser.parseColor(progressColor))
            }
        }
    }
}