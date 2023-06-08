package com.hellobike.magiccube.loader.insert

import com.hellobike.magiccube.loader.WKLoaderParam
import com.hellobike.magiccube.v2.click.*
import com.hellobike.magiccube.v2.configs.Constants

class MagicBoxEventHandler(private val param: WKLoaderParam) : IOnCubeClickListener,
    IOnCubeCallNativeListener,
    IOnCubeCountDownListener, IOnCubeCustomerOperationHandler {

    private var insert: IWKInsert? = null
    fun bindWKInsert(insert: WKInsertImpl) {
        this.insert = insert
    }

    override fun onCubeClick(url: String): Boolean {
        return param.onCubeClickListener?.onCubeClick(url) ?: false
    }

    override fun onCallNative(params: Map<String, Any?>) {
        param.onCubeCallNativeListener?.onCallNative(params)
    }

    override fun onFinish(adapter: ICountDownResultAdapter) {
        param.onCubeCountDownListener?.onFinish(adapter)
    }

    override fun onDismiss() {
        insert?.dismiss(true)
    }


}