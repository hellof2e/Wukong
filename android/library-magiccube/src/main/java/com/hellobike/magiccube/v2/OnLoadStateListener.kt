package com.hellobike.magiccube.v2

interface OnLoadStateListener {
    /**
     * 加载成功
     */
    fun onLoadSuccess()

    /**
     * @param code 错误码
     * @param msg 异常描述信息
     */
    fun onLoadFailed(code: Int, msg: String?)
}