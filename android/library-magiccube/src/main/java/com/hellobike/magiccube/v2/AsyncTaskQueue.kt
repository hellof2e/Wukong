package com.hellobike.magiccube.v2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class AsyncTaskQueue {

    companion object {
        private val JS_QUEUE: AsyncTaskQueue by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) { AsyncTaskQueue() }

        fun getJSQueue(): AsyncTaskQueue {
            return JS_QUEUE
        }
    }

    private val isRunning = AtomicBoolean(false)

    private val arrayQueue: ArrayList<Runnable> = ArrayList()


    fun clear() {
        synchronized(arrayQueue) {
            arrayQueue.clear()
        }
    }

    fun exec(runnable: Runnable) {
        synchronized(arrayQueue) {
            arrayQueue.add(runnable)
        }

        if (isRunning.compareAndSet(false, true)) {
            run()
        }
    }

    private fun run() {
        GlobalScope.launch(Dispatchers.IO) {
            while (isRunning.get()) {
                try {
                    val task = synchronized(arrayQueue) {
                        if (arrayQueue.isNotEmpty()) {
                            arrayQueue.removeAt(0)
                        } else {
                            isRunning.set(false)
                            null
                        }
                    }
                    task?.run()
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            }
        }
    }

}