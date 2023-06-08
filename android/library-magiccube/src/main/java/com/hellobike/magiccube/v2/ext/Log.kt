package com.hellobike.magiccube.v2.ext

import android.util.Log
import com.hellobike.magiccube.v2.configs.Constants.TAG
import com.hellobike.magiccube.v2.configs.MagicCube

internal fun logv(message: String, tag: String = TAG) {
    if (!MagicCube.starter.debug) return
    Log.v(tag, message)
}

internal fun logd(message: String, tag: String = TAG) {
    if (!MagicCube.starter.debug) return
    Log.d(tag, message)
}

internal fun logi(message: String, tag: String = TAG) {
    if (!MagicCube.starter.debug) return
    Log.i(tag, message)
}

internal fun logw(message: String, tag: String = TAG) {
    if (!MagicCube.starter.debug) return
    Log.w(tag, message)
}

internal fun loge(message: String, tag: String = TAG) {
    if (!MagicCube.starter.debug) return
    Log.e(tag, message)
}

internal fun logt(t: Throwable, tag: String = TAG) {
    if (!MagicCube.starter.debug) return
    loge(t.toString(), tag)
}