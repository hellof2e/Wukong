package com.hellobike.magiccube.model.contractmodel

import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.parser.LayoutParser

@TargetParser(LayoutParser::class)
class SpecialLayoutViewModel: BaseViewModel() {

    @StringProperty
    var width:String? = null
    @StringProperty
    var height:String? = null

}