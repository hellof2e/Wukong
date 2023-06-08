package com.hellobike.magiccube.utils

import java.lang.Exception
import kotlin.experimental.and

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/7 10:54 AM
 */
fun String?.isNotNullOrBlank():Boolean{
    return !this.isNullOrBlank()
}

fun ByteArray.byte2String(): String? {
    if (this == null || this.size == 0) {
        return ""
    }
    val result = StringBuilder()
    for (b in this) {
        //加0x100是因为有的b[i]的十六进制只有1位
        result.append(Integer.toString((b and 0xff.toByte()) + 0x100, 16).substring(1))
    }
    return result.toString()
}


fun String?.safe2Float(defaultValue: Float = 0f): Float = try {
    this?.toFloat() ?: defaultValue
} catch (e: Throwable) {
    defaultValue
}

fun String?.safe2FloatWithNullable(errorValue: Float = 0f, nullValue: Float = 0f): Float = try {
    this?.toFloat() ?: nullValue
} catch (e: Throwable) {
    errorValue
}


fun tryCatch(block: () -> Unit): Any? {
    try {
        return block.invoke()
    } catch (t: Throwable) {
        t.printStackTrace()
    }
    return null
}