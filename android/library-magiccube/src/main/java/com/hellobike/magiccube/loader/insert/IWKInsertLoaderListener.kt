package com.hellobike.magiccube.loader.insert

interface IWKInsertLoaderListener {

    /**
     * 加载成功
     */
    fun onLoadSuccess(wkInsert: IWKInsert)

    /**
     * 加载失败
     */
    fun onLoadFailed(code: Int, message: String?)

    /**
     * 加载结束，必然会执行
     */
    fun onLoadEnd()

    open class WKInsertLoaderListenerWrapper : IWKInsertLoaderListener {
        override fun onLoadSuccess(wkInsert: IWKInsert) {

        }

        override fun onLoadFailed(code: Int, message: String?) {
        }

        override fun onLoadEnd() {
        }
    }
}