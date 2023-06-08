package com.hellobike.magiccube.v2.click


interface IOnCubeCallNativeListener {

    fun onCallNative(params: Map<String, Any?>)

    open class IOnCubeCallNativeListenerWrapper: IOnCubeCallNativeListener {

        override fun onCallNative(params: Map<String, Any?>) {
            // do nothing
        }
    }
}