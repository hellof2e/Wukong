package com.hellobike.magiccube.loader.insert


/**
 * 插屏弹窗接口，如果不想实现所有接口可以使用 [IWKInsertDialogListener.WKInsertDialogListenerWrapper] 包装类
 */
interface IWKInsertDialogListener {

    /**
     * 插屏弹窗弹出
     */
    fun onShowing()


    /**
     * 插屏弹窗被关闭
     * @param byUser 是否是用户的点击行为导致的关闭
     */
    fun onDismiss(byUser: Boolean)

    /**
     * IOnInsertListener 包装类，可以根据自己的需求实现部分接口
     */
    public open class WKInsertDialogListenerWrapper : IWKInsertDialogListener {

        override fun onShowing() {
            // do nothing
        }

        override fun onDismiss(byUser: Boolean) {
            // do nothing
        }
    }
}