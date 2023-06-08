package com.hellobike.magiccube.model.contractmodel

import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.parser.ActiveStyleParser

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/2 8:27 PM
 */
@TargetParser(ActiveStyleParser::class)
class ActiveStyleViewModel {
    @StringProperty
    var background:String? = null
    @StringProperty
    var borderColor:String? =null
    @StringProperty
    var opacity:String? = null
}