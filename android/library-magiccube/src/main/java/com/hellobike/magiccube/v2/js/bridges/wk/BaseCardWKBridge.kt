package com.hellobike.magiccube.v2.js.bridges.wk

import android.content.Context
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.v2.MagicCard
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject
import com.hellobike.magiccube.v2.js.wrapper.WKJSArray
import java.lang.ref.WeakReference

internal open class BaseCardWKBridge {

    private var weakCard: WeakReference<MagicCard>? = null
    private var weakConfig: WeakReference<MagicConfig>? = null
    private var data: Data? = null

    protected fun getData() = data

    internal fun getContext(): Context? = weakCard?.get()?.getContext()

    protected fun getMagicCard(): MagicCard? = weakCard?.get()

    protected fun getMagicConfig(): MagicConfig? = weakConfig?.get()

    protected fun isValidMagicCard(): Boolean = UIUtils.contextIsValid(getContext())

    protected fun isSameSession(): Boolean {
        val magicCard = getMagicCard() ?: return false
        val magicConfig = getMagicConfig() ?: return false
        val session = magicConfig.sessionResult ?: return false
        return magicCard.getCardContext().isSameSessionFromInit(session)
    }

    fun bindMagicCard(magicCard: MagicCard) {
        this.weakCard = WeakReference(magicCard)
    }

    fun bindData(data: Data) {
        this.data = data
    }

    fun bindMagicConfig(magicConfig: MagicConfig) {
        this.weakConfig = WeakReference(magicConfig)
    }

    fun runOnUIThread(runnable: Runnable) {
        getMagicCard()?.getCardContext()?.runOnUiThread(runnable)
    }

    fun callMethod(obj: IWKJSObject, methodName: String, args: WKJSArray?) {
        obj.postEventQueue {
            if (!isValidMagicCard()) return@postEventQueue
            obj.executeVoidFunction(methodName, args)
        }
    }

    open fun release() {

    }
}