package com.hellobike.magiccube.widget

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.hellobike.magiccube.v2.template.ICustomWidgetTemplate

abstract class BaseCustomWidget(protected val context: Context) : IWKCustomWidget {

    internal var handler: Handler? = null

    override fun render(template: ICustomWidgetTemplate) {

    }

    override fun onAttachedToWindow() {
        // do nothing
    }

    override fun onDetachedFromWindow() {
        // do nothing
    }

    override fun onVisibilityChanged(isVisibility: Boolean) {
        // do nothing
    }

    protected fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            handler?.post(runnable)
        }
    }
}