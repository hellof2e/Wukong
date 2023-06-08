package com.hellobike.magiccube.v2

import androidx.annotation.IntDef


/**
 * 默认的更新方式，所有的逻辑都需要正常执行
 */
internal const val REASON_DEFAULT = 0

/**
 * 表示是内部主动的更新，比如 js 调用 setState 函数，部分js的生命周期不应该重复执行，比如 onUpdated 和 onCreated
 */
internal const val REASON_INNER_UPDATE = 1



@IntDef(REASON_DEFAULT, REASON_INNER_UPDATE)
@kotlin.annotation.Retention(AnnotationRetention.SOURCE)
internal annotation class RenderReason