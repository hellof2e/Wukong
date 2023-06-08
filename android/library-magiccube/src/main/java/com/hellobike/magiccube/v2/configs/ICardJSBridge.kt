package com.hellobike.magiccube.v2.configs

import com.hellobike.magiccube.v2.js.bridges.wk.IJSBridgeProto

interface ICardJSBridge {

    fun invoke(proto: IJSBridgeProto)

    fun onAttachedToWindow()

    fun onDetachedFromWindow()

    fun onCardCreated()

    fun onCardUpdated()

    fun onVisibilityChanged(isVisibility: Boolean)

    fun release()
}