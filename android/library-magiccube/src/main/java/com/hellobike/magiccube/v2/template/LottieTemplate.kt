package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.model.contractmodel.LottieViewModel

class LottieTemplate(private val lottie: LottieViewModel) : Template(lottie)  {

    override fun doScan() {
        super.doScan()
        fillTemplate(lottie.src)
        fillTemplate(lottie.fit)
    }
}