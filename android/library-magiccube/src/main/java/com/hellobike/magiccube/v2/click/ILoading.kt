package com.hellobike.magiccube.v2.click

import com.hellobike.magiccube.v2.click.dialog.AlertDescriptor


interface ILoading {

    fun showLoading(msg: String? = null)

    fun hideLoading()

    fun showToast(msg: String?)

    fun alert(desc: AlertDescriptor)
}