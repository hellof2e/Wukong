package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.model.contractmodel.ImageViewModel

class ImageTemplate(private val image: ImageViewModel) : Template(image)  {

    override fun doScan() {
        super.doScan()
        fillTemplate(image.src)
        fillTemplate(image.fit)
    }
}