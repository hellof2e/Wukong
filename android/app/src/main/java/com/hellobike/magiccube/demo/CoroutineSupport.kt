package com.hellobike.magiccube.demo

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class CoroutineSupport(parent: Job? = null): CoroutineScope {

    private val job: Job = if (parent == null){
        SupervisorJob()
    }else {
        SupervisorJob(parent)
    }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    /**
     * 取消协程上下文的job，
     * 取消后不要再使用了，
     * 如果再使用所有任务会不执行,
     * 一般在onDestroy里做
     */
    fun destroy(){
        job.cancel()
    }

    /**
     * 取消所有的子任务
     */
    fun cancelChildren(){
        job.cancelChildren()
    }
}