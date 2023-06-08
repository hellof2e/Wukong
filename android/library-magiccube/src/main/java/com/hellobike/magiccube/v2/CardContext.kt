package com.hellobike.magiccube.v2

import android.os.Handler
import android.os.Looper
import android.os.Message
import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.js.IJsEngine
import com.hellobike.magiccube.v2.node.IVNodeEngine
import com.hellobike.magiccube.v2.reports.session.SessionResult

class CardContext {

    internal var handler = Handler(Looper.getMainLooper())

    @RenderReason
    private var currentRenderReason: Int = REASON_DEFAULT

    var styleModel: StyleModel? = null

    var jsEngine: IJsEngine? = null

    var vNodeEngine: IVNodeEngine? = null

    var loadedMagicConfig: MagicConfig? = null
    var curMagicConfig: MagicConfig? = null

    fun bindStyleModel(styleModel: StyleModel) {
        this.styleModel = styleModel
    }

    // 绑定初始加载时候的配置属性
    fun bindCurMagicConfig(magicConfig: MagicConfig?) {
        this.curMagicConfig = magicConfig
    }

    // 绑定加载结束之后的配置属性
    fun bindLoadedMagicConfig(magicConfig: MagicConfig?) {
        this.loadedMagicConfig = magicConfig
    }

    // 和初始加载时候的 session 相比，指定 session 是否发生了改变
    fun isSameSessionFromInit(session: SessionResult): Boolean {
        val initLoadSession = this.curMagicConfig?.sessionResult ?: return false
        return initLoadSession.isSameSessionId(session)
    }

    fun runOnUiThread(runnable: Runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run()
        } else {
            handler.post(runnable)
        }
    }

    fun postDelayed(runnable: Runnable, what: Int, delayMillis: Long) {
        val message = Message.obtain(handler, runnable)
        message.what = what
        handler.sendMessageDelayed(message, delayMillis)
    }

    fun removeMessages(what: Int) {
        handler.removeMessages(what)
    }

    fun clearAllHandlerMessage() {
        handler.removeCallbacksAndMessages(null)
    }

    fun reset() {
        this.styleModel = null
    }

    fun bindRenderReason(@RenderReason renderReason: Int) {
        this.currentRenderReason = renderReason
    }

    @RenderReason
    fun getRenderReason(): Int {
        return currentRenderReason
    }
}