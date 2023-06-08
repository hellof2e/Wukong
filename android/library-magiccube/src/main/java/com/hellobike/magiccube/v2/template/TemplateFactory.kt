package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.model.contractmodel.*
import com.hellobike.magiccube.v2.configs.Constants

object TemplateFactory {

    fun createTemplate(baseViewModel: BaseViewModel): Template = when (baseViewModel) {
        is LayoutViewModel -> {
            if (baseViewModel.type == Constants.NODE_COUNTING) {
                CountDownTemplate(baseViewModel)
            } else if (baseViewModel.type == Constants.NODE_LIST_VIEW) {
                ListViewTemplate(baseViewModel)
            } else {
                ContainerTemplate(baseViewModel)
            }
        }
        is SpanViewModel -> {
            SpanTemplate(baseViewModel)
        }
        is TextViewModel -> {
            TextTemplate(baseViewModel)
        }
        is ImageViewModel -> {
            ImageTemplate(baseViewModel)
        }
        is LottieViewModel -> {
            LottieTemplate(baseViewModel)
        }
        is ProgressViewModel -> {
            ProgressTemplate(baseViewModel)
        }
        else -> {
            Template(baseViewModel)
        }
    }

}