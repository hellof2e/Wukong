package com.hellobike.magiccube.demo.configs

import com.hellobike.magiccube.demo.TestTemplate1
import com.hellobike.magiccube.demo.TestTemplate2
import com.hellobike.magiccube.widget.MagicBoxLayout

object GlobalConfig {

    fun applyCustomerTemplate(layout: MagicBoxLayout) {
        layout.apply {
            registerCustomerTemplate(TestTemplate1(layout.context))
            registerCustomerTemplate(TestTemplate2(context))
        }
    }

}