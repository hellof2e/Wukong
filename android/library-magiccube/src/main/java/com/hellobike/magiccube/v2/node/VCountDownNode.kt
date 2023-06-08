package com.hellobike.magiccube.v2.node

import android.app.Activity
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.parser.timer.CountDownTimerHelper
import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.v2.click.IOnCardCountDownListener
import com.hellobike.magiccube.v2.configs.Constants
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.data.ScopeData

class VCountDownNode(baseViewModel: BaseViewModel, type: String) : VNode(baseViewModel, type),
    CountDownTimerHelper.ICountDownCallback {

    private val countDownHelper = CountDownTimerHelper()
    private var callback: ICountDownCallback? = null
    private var isRunning = false

    private fun getCountingType(viewModel: LayoutViewModel): String =
            getValue(viewModel.countingType).stringValue() ?: Constants.COUNTING_TYPE_TIME

    override fun detachFromParentNode() {
        super.detachFromParentNode()
        cancelCountDown()
    }

//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        startCountDown()
//    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cancelCountDown()
    }

    override fun onVisibilityChanged(isVisibility: Boolean) {
        super.onVisibilityChanged(isVisibility)

        if (isVisibility) {
            startCountDown()
        } else {
            cancelCountDown()
        }
    }

    private fun cancelCountDown() {
        countDownHelper.cancelAll()
        isRunning = false
    }

    private fun startCountDown() {
        val layoutViewModel = baseViewModel as? LayoutViewModel ?: return

        val clockOffset = getValue(layoutViewModel.clockOffset).longValue() ?: 0L
        val deadline = getValue(layoutViewModel.deadline).longValue() ?: 0L
        var interval = getValue(layoutViewModel.interval).longValue() ?: 1000L
        var stop = getValue(layoutViewModel.stop).longValue() ?: 0
        val countingType = getCountingType(layoutViewModel)

        var millisInFuture = deadline - System.currentTimeMillis() + clockOffset
        if (millisInFuture <= 0) millisInFuture = 0

        if (template.hasChanged || !isRunning) {
            // 先取消被复用的
            cancelCountDown()

            if (stop <= 0) stop = 0
            if (interval <= 1000) interval = 1000

            if (millisInFuture <= stop) {
                if (widget is IOnCardCountDownListener) {
                    (widget as? IOnCardCountDownListener)?.onExpire()
                }
                return
            }

//            logd("初始化 >> clockOffset: $clockOffset deadline: $deadline interval: $interval stop: $stop countingType: $countingType")
//            logd("开始倒计时 >> millisInFuture: $millisInFuture countDownInterval: $interval")

            // 开启倒计时
            countDownHelper.startCountDown(millisInFuture, interval, stop, this)
            isRunning = true
        }
    }

    override fun renderEnd() {
        super.renderEnd()
        startCountDown()
    }


    override fun scanTemplate() {
        super.scanTemplate()

        val layoutViewModel = baseViewModel as? LayoutViewModel ?: return

        val clockOffset = getValue(layoutViewModel.clockOffset).longValue() ?: 0L
        val deadline = getValue(layoutViewModel.deadline).longValue() ?: 0L
        val countingType = getCountingType(layoutViewModel)

        var millisInFuture = deadline - System.currentTimeMillis() + clockOffset
        if (millisInFuture <= 0) millisInFuture = 0

        bindTimerData(countingType, millisInFuture)
    }
    override fun onFinish() {
        val viewModel = baseViewModel as? LayoutViewModel ?: return
        val countingType = getCountingType(viewModel)
        bindTimerData(countingType, 0)
        reDiff(true)
    }

    override fun onTick(millisUntilFinished: Long) {
        val viewModel = baseViewModel as? LayoutViewModel ?: return
        val countingType = getCountingType(viewModel)
        bindTimerData(countingType, millisUntilFinished)
        reDiff(false)
    }

    private fun bindTimerData(type: String, time: Long) {
        if (scopeData != null) {
            scopeData?.bindCountDownData(type, time)
        } else {
            wholeData?.bindCountDownData(type, time)
        }
    }


    private fun reDiff(isFinish: Boolean) {
        val view = widget?.getView()
        if (view == null) {
            cancelCountDown()
        }

        if (view?.context is Activity) {
            val activity = view.context as Activity

            if (activity.isFinishing) {
                cancelCountDown()
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed) {
                cancelCountDown()
            }
        }

        tryCatch {
            callback?.reDiff(this, wholeData!!, scopeData)
            if (isFinish && widget is IOnCardCountDownListener) {
                (widget as? IOnCardCountDownListener)?.onFinish()
            }
        }
    }

    fun setOnCountDownCallback(callback: ICountDownCallback) {
        this.callback = callback
    }

    interface ICountDownCallback {
        fun reDiff(node: VNode, data: Data, scopeData: ScopeData?)
    }

}