package com.hellobike.magiccube.v2

import android.content.Context
import android.view.ViewGroup
import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.data.Data
import com.hellobike.magiccube.v2.js.IJsEngine
import com.hellobike.magiccube.v2.js.JSEngineInitializer
import com.hellobike.magiccube.v2.node.IVNodeEngine
import com.hellobike.magiccube.v2.node.VNode
import com.hellobike.magiccube.v2.node.VNodeParserV2
import com.hellobike.magiccube.widget.IOnWidgetAttachToWindowChanged
import com.hellobike.magiccube.widget.IWidgetAttachToWindow
import com.hellobike.magiccube.v2.preload.IMetaData
import com.hellobike.magiccube.v2.reports.Codes
import com.hellobike.magiccube.v2.reports.Msgs
import java.lang.ref.WeakReference
import kotlin.collections.HashMap

internal class MagicCard(view: ViewGroup) {

    private val viewGroup: WeakReference<ViewGroup> = WeakReference(view)

    private val vNodeEngine: IVNodeEngine = VNodeParserV2.create()

    private val cardContext: CardContext = CardContext().apply {
        this.vNodeEngine = this@MagicCard.vNodeEngine
    }

    private val cardService: CardService = CardService(cardContext)

    private var vNodeTree: VNode? = null // 虚拟节点树

    private var onLoadStateListener: OnLoadStateListener? = null

    fun getCardContext() = cardContext

    fun getContext(): Context? = viewGroup.get()?.context

    fun setOnLoadStateListener(listener: OnLoadStateListener) {
        this.onLoadStateListener = listener
    }

    // 这种更新能力主要用在在js逻辑层来主动更新数据，此时所有的东西都要复用，尤其是js引擎。否则生命周期会重复执行
    fun updateData(url: String, data: HashMap<String, Any?>) {
        val lastStyleModel = this.cardContext.styleModel ?: return
        val lastMagicConfig = this.cardContext.curMagicConfig ?: return
        val jsEngine = this.cardContext.jsEngine ?: return
        if (url != lastStyleModel.styleUrl) return // url 发生切换，或许被复用了
        val dataSource = Data(data)
        if (lastStyleModel.isValidViewModel()) {
            lastMagicConfig.sessionResult?.reloadSessionId()
            onStyleLoadCompleteV2(
                lastStyleModel,
                jsEngine,
                lastMagicConfig,
                dataSource,
                REASON_INNER_UPDATE
            )
        }
    }

    fun loadByMetaData(metaData: IMetaData, magicConfig: MagicConfig) {
        val url = metaData.getUrl()
        val data =  metaData.getData()
        val styleModel = metaData.getStyle()
        val jsEngine = metaData.getJS()

        if (metaData.hasSuccess() && styleModel?.isValidViewModel() == true && jsEngine != null) {

            val dataSource = Data(metaData.getData())

            val lastStyleModel = this.cardContext.styleModel
            val lastVNodeTree = this.vNodeTree

            if (lastStyleModel?.isValidViewModel() == true
                && lastStyleModel.styleUrl == url
                && lastStyleModel.layoutModel != null
                && lastVNodeTree?.wholeData?.originData() == dataSource.originData()
            ) { // 数据没有发生任何变化变化
                onLoadStateListener?.onLoadSuccess()
                return
            }
            JSEngineInitializer.initJSEngine(jsEngine, magicConfig, this)
            // 保存当前流程的session，供以后异步回调之后判断session是否发生改变
            cardContext.bindCurMagicConfig(magicConfig)
            onStyleLoadCompleteV2(styleModel, jsEngine, magicConfig, dataSource, REASON_DEFAULT)
        } else {
            loadByUrl(url, data, magicConfig)
        }
    }

    fun loadByUrl(
        url: String?,
        data: HashMap<String, Any?>?,
        magicConfig: MagicConfig,
        reload: Boolean = false
    ) {
        if (url.isNullOrBlank()) { // url 是空
            onLoadStateListener?.onLoadFailed(Codes.INVALID_URL, Msgs.INVALID_URL)
            return
        }

        if (data == null) { // 数据是空
            onLoadStateListener?.onLoadFailed(Codes.INVALID_DATA, Msgs.INVALID_DATA)
            return
        }

        val dataSource = Data(data)

        val lastStyleModel = this.cardContext.styleModel
        val lastVNodeTree = this.vNodeTree


        if (!reload && lastStyleModel?.isValidViewModel() == true
            && lastStyleModel.styleUrl == url
            && lastStyleModel.layoutModel != null
            && lastVNodeTree?.wholeData?.originData() == dataSource.originData()
        ) { // 数据没有发生任何变化变化
            onLoadStateListener?.onLoadSuccess()
            return
        }

        // 保存当前流程的session，供以后异步回调之后判断session是否发生改变
        cardContext.bindCurMagicConfig(magicConfig)

        // 不管是复用的时候，还是初次创建的时候，只要load就需要重新创建一个js引擎
        val jsEngineInitializer = JSEngineInitializer()
            .installMagicCard(this)
            .installMagicConfig(magicConfig)
            .installData(dataSource)

        cardService.loadStyleV2(
            url, jsEngineInitializer,
            { styleModel, jsEngine, newData ->
                if (!newData.isNullOrEmpty()) { // 数据发生改变，通知出去
                    dataSource.resetData(newData)
                    magicConfig.magicParams?.onCubeDataChangedListener?.onDataUpdated(dataSource.originHashMapData())
                }
                onStyleLoadCompleteV2(styleModel, jsEngine, magicConfig, dataSource, REASON_DEFAULT)
            }, { _, code, message ->
                onStyleLoadFailedV2(code, message, magicConfig)
            })
    }

    private fun onStyleLoadFailedV2(code: Int, message: String?, magicConfig: MagicConfig) =
        cardContext.runOnUiThread {
            if (viewGroup.get() == null || !UIUtils.contextIsValid(getContext())) {
                return@runOnUiThread // 被释放了，或者页面被关闭
            }
            val curSession = cardContext.curMagicConfig?.sessionResult
            val session = magicConfig.sessionResult
            if (curSession != null && session != null && !session.isSameSessionId(curSession)) {
                return@runOnUiThread
            }
            onLoadStateListener?.onLoadFailed(code, message)
        }

    private fun onStyleLoadCompleteV2(
            style: StyleModel, jsEngine: IJsEngine, magicConfig: MagicConfig, data: Data, @RenderReason renderReason: Int
    ) = cardContext.runOnUiThread {
        val curSession = cardContext.curMagicConfig?.sessionResult
        val session = magicConfig.sessionResult
        if (curSession != null && session != null && !session.isSameSessionId(curSession)) {
            return@runOnUiThread
        }
        if (viewGroup.get() == null || !UIUtils.contextIsValid(getContext())) {
            return@runOnUiThread // 被释放了，或者页面被关闭
        }
        handleStyleLoadCompleteV2(style, magicConfig, jsEngine, data, renderReason)
    }

    private fun reset() = cardContext.runOnUiThread {
        this.vNodeTree?.detachFromParentNode()
        this.vNodeTree = null
        this.cardContext.reset()
    }


    private fun handleStyleLoadCompleteV2(
            style: StyleModel, magicConfig: MagicConfig, jsEngine: IJsEngine, data: Data, @RenderReason renderReason: Int
    ) {
        val lastJSEngine = cardContext.jsEngine
        cardContext.jsEngine = jsEngine
        cardContext.bindLoadedMagicConfig(magicConfig)

        if (this.cardContext.styleModel?.styleUrl != style.styleUrl) { // url 发生改变直接重置状态
            lastJSEngine?.release()
            reset()
        }

        val layoutModel = style.layoutModel
        if (layoutModel != null) { // ViewModel 已经存在，直接使用
            vNodeEngine.injectCardContext(cardContext)
            handleViewModelParseCompleteV2(style, layoutModel, magicConfig, data, renderReason)
        } else {
            onLoadStateListener?.onLoadFailed(
                Codes.ERROR_PARSE_VIEW_MODEL, "ViewModel 没有解析"
            )
        }
    }

    // viewModel 解析成功
    private fun handleViewModelParseCompleteV2(
            style: StyleModel, viewModel: LayoutViewModel, magicConfig: MagicConfig, data: Data, @RenderReason renderReason: Int
    ) {
        val lastVNodeTree = this.vNodeTree
        if (lastVNodeTree == null) { // 新建
            createVNode(style, viewModel, magicConfig, data, renderReason)
        } else {  // 复用
            diffVNode(lastVNodeTree, style, magicConfig, data, renderReason)
        }
    }

    private fun createVNode(
            style: StyleModel, viewModel: LayoutViewModel, magicConfig: MagicConfig, data: Data, @RenderReason renderReason: Int
    ) {
        var throwable: Throwable? = null
        val newTree = try {
            vNodeEngine.parse(viewModel, data)
        } catch (t: Throwable) {
            t.printStackTrace()
            throwable = t
            null
        }

        if (newTree == null) { // VNode 解析失败
            onLoadStateListener?.onLoadFailed(
                Codes.ERROR_PARSE_NODE_FAILED, throwable?.toString() ?: "虚拟节点解析失败"
            )
            return
        }

        cardContext.bindRenderReason(renderReason)
        createRender(style, newTree, magicConfig, renderReason)
    }

    private fun createRender(style: StyleModel, vNodeTree: VNode, magicConfig: MagicConfig, @RenderReason renderReason: Int) {
        val viewGroup = viewGroup.get() ?: return

        try {
            val widget = vNodeEngine.getRender()
                    .injectMagicConfig(magicConfig)
                    .injectCardContext(cardContext)
                    .doRender(viewGroup.context, vNodeTree)

            viewGroup.removeAllViews()
            val view = widget.getView()

            if (view is IWidgetAttachToWindow) {
                view.setWidgetAttachToWindowChanged(object : IOnWidgetAttachToWindowChanged {
                    override fun onAttachedToWindow() {
                        vNodeTree.onAttachedToWindow()
                        vNodeTree.vNodeContext?.traceShow()
                        cardContext.jsEngine?.onAttachedToWindow()
                    }

                    override fun onDetachedFromWindow() {
                        vNodeTree.onDetachedFromWindow()
                        cardContext.jsEngine?.onDetachedFromWindow()
                    }

                    override fun onVisibilityChanged(isVisibility: Boolean) {
                        vNodeTree.onVisibilityChanged(isVisibility)
                        cardContext.jsEngine?.onVisibilityChanged(isVisibility)
                    }
                })
            }
            viewGroup.addView(view)

            this.vNodeTree = vNodeTree
            this.cardContext.bindStyleModel(style)
            onLoadStateListener?.onLoadSuccess()

            // 渲染成功
            if (style.layoutModel?.enableJSLifecycle() == true && renderReason == REASON_DEFAULT) {
                this.cardContext.jsEngine?.callOnCreated()
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            onLoadStateListener?.onLoadFailed(Codes.ERROR_PARSE_VIEW, t.toString())
        }
    }

    private fun diffVNode(vNodeTree: VNode, style: StyleModel, magicConfig: MagicConfig, data: Data, @RenderReason renderReason: Int) {

        val parseVNodeSuccess = try {
            vNodeEngine.diff(vNodeTree, data)
            true
        } catch (t: Throwable) {
            t.printStackTrace()
            onLoadStateListener?.onLoadFailed(Codes.ERROR_PARSE_NODE_FAILED, t.toString())
            false
        }

        if (!parseVNodeSuccess) return // 解析失败

        cardContext.bindRenderReason(renderReason)
        diffRender(style, vNodeTree, magicConfig, renderReason)
    }

    private fun diffRender(style: StyleModel, vNodeTree: VNode, magicConfig: MagicConfig, @RenderReason renderReason: Int) {
        val viewGroup = viewGroup.get() ?: return

        try {
            vNodeEngine.getRender()
                    .injectMagicConfig(magicConfig)
                    .injectCardContext(cardContext)
                    .doRender(viewGroup.context, vNodeTree)

            this.cardContext.bindStyleModel(style)
            this.vNodeTree = vNodeTree
            onLoadStateListener?.onLoadSuccess()

            // 渲染成功
            if (style.layoutModel?.enableJSLifecycle() == true && renderReason == REASON_DEFAULT) {
                this.cardContext.jsEngine?.callOnUpdated()
            }

        } catch (t: Throwable) {
            t.printStackTrace()
            onLoadStateListener?.onLoadFailed(Codes.ERROR_PARSE_VIEW, t.toString())
        }
    }

    fun onDestroy() {
        cardContext.jsEngine?.release()
        cardContext.jsEngine = null
        reset()
        cardContext.clearAllHandlerMessage()
    }
}