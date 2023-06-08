package com.hellobike.magiccube.v2.click

/**
 * 卡片点击接口
 */
interface IOnCubeClickListener {
    /**
     * 卡片点击事件
     * @param url 点击url
     * @return true 是否拦截事件。true 拦截事件，false 不拦截事件
     */
    fun onCubeClick(url: String): Boolean

    open class OnCubeClickListenerWrapper : IOnCubeClickListener {

        override fun onCubeClick(url: String): Boolean {
            // do nothing
            return false
        }
    }
}