package com.hellobike.magiccube.v2.configs

import android.os.Handler
import android.os.Looper
import com.hellobike.magiccube.v2.data.SafeMap

object Constants {
    const val TAG = "HBAndroidDSLSDK"

    val MAIN_HANDLER = Handler(Looper.getMainLooper())

    val NULL_VALUE = SafeMap(null)

    const val NODE_TYPE_FOR = "m-for"
    const val NODE_TYPE_IF = "m-if"
    const val NODE_COUNTING = "counting"
    const val NODE_LIST_VIEW = "list"
    const val NODE_LIST_ITEM = "item"

    const val COUNTING_TYPE_TIME = "time"
    const val COUNTING_TYPE_DATETIME = "datetime"

    const val KEY_COUNT_DOWN_TIMER = "MC_TIMER"

    const val KEY_SIZE = "size()"

    const val KEY_IS_EMPTY = "isNullOrEmpty()"
}