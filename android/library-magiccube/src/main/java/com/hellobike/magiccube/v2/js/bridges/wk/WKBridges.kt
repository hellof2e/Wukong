package com.hellobike.magiccube.v2.js.bridges.wk

import com.hellobike.magiccube.v2.configs.MagicCube

internal object WKBridges {

    fun createBridges(): List<IWKVoidBridge> {
        val bridges = mutableListOf<IWKVoidBridge>()
        bridges.add(WKAjaxBridge())
        bridges.add(WKSetStateBridge())
        bridges.add(WKCallNativeBridge())
        bridges.add(WKSetTimeoutBridge())
        bridges.add(WKShowWukongDialogBridge())
        bridges.add(WKDismissBridge())
        MagicCube.starter.wkJSBridges.forEach { bridges.add(ExternalWKBridge(it.key, it.value)) }
        return bridges
    }
}