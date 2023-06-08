package com.hellobike.magiccube.parser.timer

import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class CountDownTimerHelper {

    // timer 列表
    // key: countDownInterval
    private val timers: ConcurrentHashMap<String, CountDownTimerManager> = ConcurrentHashMap()


    private fun generateUniqueId(millsInFuture: Long, interval: Long, stop: Long): String =
        "${millsInFuture}_${interval}_${stop}"

    // 开始倒计时
    fun startCountDown(millsInFuture: Long, interval: Long, stop: Long, callback: ICountDownCallback) {
        synchronized(this) {

            val id = generateUniqueId(millsInFuture, interval, stop)

            // 通过 id 查询 timer
            var timer = timers[id]
            if (timer != null) {
                timer.setCallback(callback)
                return
            }


            // 启动一个新的定时器
            timer = CountDownTimerManager(millsInFuture, interval, stop)
            timer.setCallback(callback)
            timer.start()
            timers[id] = timer
        }
    }


    // 手动取消
    fun cancel(millsInFuture: Long, interval: Long, stop: Long) {
        synchronized(this) {
            val id = generateUniqueId(millsInFuture, interval, stop)

            val countDownTimerManager = timers[id]
            if (countDownTimerManager != null) {
                countDownTimerManager.cancel()
                timers.remove(id)
            }
        }
    }

    fun cancelAll() {
        synchronized(this) {
            if (timers.isNullOrEmpty()) return

            for (timer in timers) {
                timer.value.cancel()
            }

            timers.clear()
        }
    }


    inner class CountDownTimerManager(
        private val millisInFuture: Long,
        private val countDownInterval: Long,
        private val stop: Long

    ) :
        MCCountDownTimer(millisInFuture, countDownInterval, stop) {

        private var weakCallback: WeakReference<ICountDownCallback>? = null

        fun setCallback(callback: ICountDownCallback) {
            weakCallback = WeakReference(callback)
        }

        override fun onTick(millisUntilFinished: Long) {
            val callback = weakCallback?.get()
            if (callback == null) {
                cancel(millisInFuture, countDownInterval, stop) // 取消并移除
            } else {
                callback.onTick(millisUntilFinished)
            }
        }

        override fun onFinish() {
            weakCallback?.get()?.onFinish()
            cancel(millisInFuture, countDownInterval, stop) // 取消并移除
        }
    }


    interface ICountDownCallback {
        fun onTick(millisUntilFinished: Long)
        fun onFinish()
    }


}