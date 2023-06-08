package com.hellobike.magiccube.parser.widget

import android.content.Context
import com.hellobike.magiccube.v2.click.ICountDownResultAdapter
import com.hellobike.magiccube.v2.click.IOnCardCountDownListener

class CountDownWidget(context: Context) : ContainerWidget(context), IOnCardCountDownListener {

    override fun onFinish() {
        val template = template
        if (template != null) {
            val adapter = ICountDownResultAdapter.CountDownResultAdapterImpl(template, ICountDownResultAdapter.FINISHED)
            this.getMagicConfig()?.onCubeCountDownListener?.onFinish(adapter)

            handleJSLogicScript(template.getViewModel().action?.countingFinishEvent, template)
        }
    }

    override fun onExpire() {
        val template = template
        if (template != null) {
            val adapter = ICountDownResultAdapter.CountDownResultAdapterImpl(template, ICountDownResultAdapter.EXPIRE)
            this.getMagicConfig()?.onCubeCountDownListener?.onFinish(adapter)
        }
    }
}