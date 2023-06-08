package com.hellobike.magiccube.loader

import android.view.Gravity
import com.hellobike.magiccube.v2.click.*

class WKLoaderParam private constructor(
        builder: Builder
) {

    internal var data = builder.data
    internal var style = builder.style

    internal var dimAmount: Float = builder.dimAmount
    internal var gravity: Int = builder.gravity
    internal var canceledOnTouchOutside: Boolean = builder.canceledOnTouchOutside

    internal var onCubeClickListener = builder.onCubeClickListener
    internal var onCubeCountDownListener = builder.onCubeCountDownListener
    internal var onCubeCallNativeListener = builder.onCubeCallNativeListener

    class Builder {

        internal var data: HashMap<String, Any?>? = null
        internal var style: String? = null

        internal var dimAmount: Float = 0.5f
        internal var gravity: Int = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        internal var canceledOnTouchOutside: Boolean = true

        internal var onCubeClickListener: IOnCubeClickListener? = null
        internal var onCubeCountDownListener: IOnCubeCountDownListener? = null
        internal var onCubeCallNativeListener: IOnCubeCallNativeListener? = null


        fun bindData(data: HashMap<String, Any?>?): Builder {
            this.data = data
            return this
        }

        fun bindStyle(style: String?): Builder {
            this.style = style
            return this
        }

        fun bindGravity(gravity: Int): Builder {
            this.gravity = gravity
            return this
        }

        fun bindCanceledOnTouchOutside(cancel: Boolean): Builder {
            this.canceledOnTouchOutside = cancel;
            return this
        }

        fun bindDimAmount(dimAmount: Float): Builder {
            this.dimAmount = when {
                dimAmount <= 0 -> {
                    0f
                }
                dimAmount >= 1 -> {
                    1.0f
                }
                else -> dimAmount
            }
            return this
        }

        fun bindOnCubeClickListener(listener: IOnCubeClickListener): Builder {
            this.onCubeClickListener = listener
            return this
        }

        fun bindOnCubeCountDownListener(listener: IOnCubeCountDownListener): Builder {
            this.onCubeCountDownListener = listener
            return this
        }

        fun bindOnCubeCallNativeListener(listener: IOnCubeCallNativeListener): Builder {
            this.onCubeCallNativeListener = listener
            return this
        }

        fun build(): WKLoaderParam {
            return WKLoaderParam(this)
        }
    }
}