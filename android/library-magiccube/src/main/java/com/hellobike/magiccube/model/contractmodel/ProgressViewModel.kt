package com.hellobike.magiccube.model.contractmodel

import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.parser.ProgressParser

@TargetParser(ProgressParser::class)
class ProgressViewModel : BaseViewModel() {

    @StringProperty
    var progress: String? = null // 当前进度

    @StringProperty
    var maxProgress: String? = null // 最大进度

    @StringProperty
    var progressColor: String? = null // 进度颜色
}