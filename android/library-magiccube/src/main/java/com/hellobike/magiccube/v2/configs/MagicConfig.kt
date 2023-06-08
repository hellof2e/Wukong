package com.hellobike.magiccube.v2.configs

import com.hellobike.magiccube.v2.click.*
import com.hellobike.magiccube.v2.reports.session.SessionResult
import com.hellobike.magiccube.widget.BaseCustomWidget
import java.util.concurrent.ConcurrentHashMap

class MagicConfig private constructor(builder: Builder) {

    internal var templateFactory = builder.templateFactory
    internal var sessionResult: SessionResult? = builder.session
    internal var onCardClickListener: IOnCardClickListener? = builder.onCardClickListener
    internal val onCubeCountDownListener: IOnCubeCountDownListener? = builder.onCubeCountDownListener
    internal var magicParams: MagicInfo? = builder.magicParams
    internal var loadingHandler: ILoading? = builder.loadingHandler

    internal var styleUrl: String? = builder.styleUrl

    internal var data: Map<String, Any?>? = builder.data

    class Builder {

        internal var templateFactory: ConcurrentHashMap<String, BaseCustomWidget>? = null
        internal var session: SessionResult? = null
        internal var onCardClickListener: IOnCardClickListener? = null
        internal var onCubeCountDownListener: IOnCubeCountDownListener? = null
        internal var magicParams: MagicInfo? = null
        internal var loadingHandler: ILoading? = null
        internal var styleUrl: String? = null

        internal var data: Map<String, Any?>? = null

        fun injectTemplateFactory(templateFactory: ConcurrentHashMap<String, BaseCustomWidget>): Builder {
            this.templateFactory = templateFactory
            return this
        }

        fun injectSession(session: SessionResult): Builder {
            this.session = session
            return this
        }

        fun injectOnCardClickListener(listener: IOnCardClickListener): Builder {
            this.onCardClickListener = listener
            return this
        }

        fun injectMagicParams(magicInfo: MagicInfo?): Builder {
            this.magicParams = magicInfo
            return this
        }

        fun injectStyleUrl(url: String?): Builder {
            this.styleUrl = url
            return this
        }

        fun bindOnCubeCountDownListener(listener: IOnCubeCountDownListener?): Builder {
            this.onCubeCountDownListener = listener
            return this
        }

        fun injectLoadingHandler(loading: ILoading): Builder {
            this.loadingHandler = loading
            return this
        }

        fun injectData(data: Map<String, Any?>?): Builder {
            this.data = data
            return this
        }

        fun build(): MagicConfig {
            return MagicConfig(this)
        }
    }
}