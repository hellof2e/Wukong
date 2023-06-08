package com.hellobike.magiccube.model.contractmodel

import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.parser.ImageParser

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/2 10:58 AM
 */
@TargetParser(ImageParser::class)
class ImageViewModel: BaseViewModel() {
    @StringProperty
    var src:String? = null
    @StringProperty
    var fit:String? = null
}