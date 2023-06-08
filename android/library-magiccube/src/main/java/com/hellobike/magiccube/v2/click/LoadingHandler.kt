package com.hellobike.magiccube.v2.click

import android.app.Activity
import android.app.Dialog
import android.content.Context
import com.hellobike.magiccube.v2.click.dialog.AlertDescriptor
import java.lang.ref.WeakReference

internal class LoadingHandler(context: Context) : ILoading {

    private val weakContext = WeakReference(context)

    private fun getContext(): Context? = weakContext.get()


    override fun showLoading(msg: String?) {

    }

    override fun hideLoading() {

    }

    override fun showToast(msg: String?) {

    }

    override fun alert(desc: AlertDescriptor) {


    }

    private fun checkContext(): Context? {
        val context = getContext() ?: return null

        if (context is Activity) {

            if (context.isFinishing) {
                return null
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && context.isDestroyed) {
                return null
            }
        }
        return context
    }
}