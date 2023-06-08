package com.hellobike.magiccube.v2.click

interface IOnCubeCountDownListener {

    /**
     * 倒计时完成
     * @param adapter 可以通过 adapter 获取相关数据，以及相关 code 码
     */
    fun onFinish(adapter: ICountDownResultAdapter)

    open class OnCubeCountDownListenerWrapper : IOnCubeCountDownListener {

        override fun onFinish(adapter: ICountDownResultAdapter) {
            // do nothing
        }
    }
}