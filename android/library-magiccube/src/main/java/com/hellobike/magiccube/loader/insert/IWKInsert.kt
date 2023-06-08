package com.hellobike.magiccube.loader.insert

interface IWKInsert {

    /**
     * 设置插屏弹窗操作的监听
     * @param listener 结果监听 [IWKInsertDialogListener]
     */
    fun setOnInsertDialogListener(listener: IWKInsertDialogListener)

    /**
     * 开始弹出
     */
    fun process()

    /**
     * 弹窗是否正在展示
     */
    fun isShowing(): Boolean

    /**
     * 关闭，默认位非用户的点击行为导致的关闭
     */
    fun dismiss()

    /**
     * 关闭
     * @param byUser 是否是用户的点击交互导致的关闭行为
     */
    fun dismiss(byUser: Boolean)
}