package com.hellobike.magiccube.v2.click

import com.hellobike.magiccube.v2.template.Template

interface ICountDownResultAdapter {

    companion object {
        const val FINISHED = 0 // 倒计时结束
        const val EXPIRE = 3001 // 倒计时已过期
    }

    /**
     * @return 全量数据
     */
    fun getData(): Map<String, Any?>?

    /**
     * @return 作用域数据
     */
    fun getScopeData(): Map<String, Any?>?

    /**
     * 获取当前倒计时的状态
     * @return 状态码。 倒计时完成: [ICountDownResultAdapter.FINISHED]  倒计时已经过期: [ICountDownResultAdapter.EXPIRE]
     */
    fun getCode(): Int


    class CountDownResultAdapterImpl(private val template: Template, private val code: Int) :
        ICountDownResultAdapter {

        override fun getData(): Map<String, Any?>? =
            template.getWholeData()?.originData() as? Map<String, Any?>

        override fun getScopeData(): Map<String, Any?>? =
            template.getScopeData()?.originData() as? Map<String, Any?>

        override fun getCode(): Int = code

    }
}