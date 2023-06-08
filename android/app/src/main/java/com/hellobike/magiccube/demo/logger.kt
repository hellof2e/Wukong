package com.hellobike.magiccube.demo

import android.util.Log

fun logd(message: String, tag: String = "HBAndroidDSLSDK") {
    Log.d(tag, message)
}

fun loge(message: String, tag: String = "HBAndroidDSLSDK") {
    Log.e(tag, message)
}