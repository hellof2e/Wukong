package com.hellobike.magiccube.v2.configs

import com.hellobike.magiccube.v2.click.*
import com.hellobike.magiccube.v2.preload.IMetaData


class MagicInfo private constructor(builder: Builder) {

    internal val onCubeClickListener: IOnCubeClickListener? = builder.onCubeClickListener
    internal val onCubeCountDownListener: IOnCubeCountDownListener? = builder.onCubeCountDownListener
    internal val onCubeCallNativeListener: IOnCubeCallNativeListener? = builder.onCubeCallNativeListener
    internal val onCubeCustomerOperationHandler: IOnCubeCustomerOperationHandler? = builder.onCubeCustomerOperationHandler
    internal val onCubeDataChangedListener: IOnCubeDataChangedListener? = builder.onCubeDataChangedListener
    internal val metaData: IMetaData? = builder.metaData
    internal val reload: Boolean = builder.reload

    class Builder {
        internal var onCubeClickListener: IOnCubeClickListener? = null
        internal var onCubeCountDownListener: IOnCubeCountDownListener? = null
        internal var onCubeCallNativeListener: IOnCubeCallNativeListener? = null
        internal var onCubeCustomerOperationHandler: IOnCubeCustomerOperationHandler? = null
        internal var onCubeDataChangedListener: IOnCubeDataChangedListener? = null
        internal var metaData: IMetaData? = null
        internal var reload: Boolean = false

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

        fun bindOnCubeDataChangedListener(listener: IOnCubeDataChangedListener): Builder {
            this.onCubeDataChangedListener = listener
            return this
        }

        fun bindMetaData(metaData: IMetaData?): Builder {
            this.metaData = metaData
            return this
        }

        fun build(): MagicInfo {
            return MagicInfo(this)
        }
    }
}