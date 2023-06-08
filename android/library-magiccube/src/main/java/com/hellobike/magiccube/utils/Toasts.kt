package com.hellobike.magiccube.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

object Toasts {
    private var mToast: Toast? = null

    /**
     *  如果 mToast 没有初始化, 就创建一个 Toast, 并赋值
     *  否则就直接显示
     */
    private fun <T : Context> showToast(context: T, message: String, duration: Int) {
        mToast?.let {
            it.duration = duration
            it.setText(message)
            it.show()
        } ?: Toast.makeText(context.applicationContext, message, duration).apply {
            mToast = this
            show()
        }
    }

    /**
     *  防止重复 showToast 显示
     *  如果 mToast不为空 就显示, 否则就创建新的 mToast
     */
    fun Context.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        showToast(this, message, duration)
    }

    fun Context.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
        toast(getString(message), duration)
    }

    fun Activity.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        showToast(this, message, duration)
    }

    fun Activity.toast(@StringRes message: Int, duration: Int = Toast.LENGTH_SHORT) {
        toast(getString(message), duration)
    }

    fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        context?.let { showToast(it, message, duration) }
    }

    fun Fragment.toast(@StringRes strRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        toast(getString(strRes), duration)
    }

    fun View.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
        context?.let { showToast(it, message, duration) }
    }

    fun View.toast(@StringRes strRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        toast(context.getString(strRes), duration)
    }
}