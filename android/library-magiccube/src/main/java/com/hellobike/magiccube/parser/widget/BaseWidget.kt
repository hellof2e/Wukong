package com.hellobike.magiccube.parser.widget

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.hellobike.magiccube.model.contractmodel.*
import com.hellobike.magiccube.parser.DSLParser.parseValue
import com.hellobike.magiccube.parser.engine.*
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.v2.CardContext
import com.hellobike.magiccube.v2.configs.MagicConfig
import com.hellobike.magiccube.v2.configs.MagicCube
import com.hellobike.magiccube.v2.template.Grammar
import com.hellobike.magiccube.v2.template.Template
import com.hellobike.magiccube.widget.IBorder
import com.hellobike.magiccube.widget.IParent
import com.hellobike.magiccube.widget.IPressedStateChangedListener

abstract class BaseWidget<VIEW : View>(protected val context: Context) : IWidget,
    IPressedStateChangedListener {

    private lateinit var mView: VIEW
    protected lateinit var viewEngine: IViewEngine

    /**
     * 当前 widget 关联到的父 widget
     */
    protected var parent: ContainerWidget? = null

    /**
     * 当前节点的 node
     */
    private var nodeAdapter: INodeAdapter? = null

    var template: Template? = null

    private var config: MagicConfig? = null

    private var cardContext: CardContext? = null

    protected abstract fun initView(): VIEW

    override fun getView(): VIEW = mView

    override fun getParentWidget(): ContainerWidget? {
        return parent
    }

    protected fun getMagicConfig(): MagicConfig? = cardContext?.loadedMagicConfig

    protected fun initEngine(engine: Engine?) {
//        if (this.mView != null) {
//            (this.mView.parent as? ViewGroup)?.removeView(this.mView)
//        }
        this.viewEngine = ViewEngineManager.createViewEngine(context, engine)
        this.mView = initView()
    }

    /**
     * 将此 Widget 关联到父 Widget,只是一个弱关联，为了能够通过父Widget创建子节点的Node
     */
    override fun attachToParent(parent: ContainerWidget?) {
        if (this.parent != parent) {
            this.parent = parent
            initEngine(parent?.engine)
        }
    }

    /**
     * 获取当前节点的 YogaNode， 如果node不存在会尝试创建一个 YogaNode，
     * 在调用该方法之前首先需要调用 attachToParent 方法，关联到父Widget中。
     */
    override fun getNodeAdapter(): INodeAdapter? {
        if (parent == null) return null
        if (nodeAdapter == null) {
            nodeAdapter = viewEngine.createYogaNode(parent!!.getView(), getView())
        }
        return nodeAdapter
    }

    /**
     * 应用 style
     */
    override fun applyStyle(style: StyleViewModel) {
        viewEngine.applyStyle(getView(), style, getNodeAdapter())
    }

    /**
     * 应用 layout
     */
    override fun applyLayout(layout: LayoutViewModel) {
        viewEngine.applyLayout(getView(), layout, getNodeAdapter())
    }

    override fun markDirty() {
        if (parent?.getView() is IParent) {
            (parent?.getView() as? IParent)?.markDirty(getView())
        }
    }

    override fun removeViewFromParent() {
        if (getView().parent is ViewGroup) {
            (getView().parent as ViewGroup).removeView(getView())
        }
    }

    override fun bindTemplate(template: Template) {
        this.template = template
    }

    override fun bindCardContext(context: CardContext?) {
        this.cardContext = context
    }

    private fun doRenderStart() {
        // do nothing
    }

    private fun doRenderEnd() {
        // do nothing
    }

    override fun doRender(baseViewModel: BaseViewModel, template: Template) {
        if (baseViewModel.layout != null) applyLayout(baseViewModel.layout!!)
        if (baseViewModel.style != null) applyStyle(baseViewModel.style!!)

//        logv("hasChanged >> ${baseViewModel.type} ${template.hasChanged}")
        if (template.hasChanged) {
            doRenderStart()
            render(baseViewModel, template)
            doRenderEnd()
        }

        renderAction(baseViewModel, template)
    }

    protected open fun render(baseViewModel: BaseViewModel, template: Template) {
        if (getView() !is ViewGroup) {
            markDirty()
        }

        val view = getView()
        if (view is ViewGroup && baseViewModel.layout is LayoutViewModel) {
            val clipChildren = baseViewModel.layout?.clipChildren ?: true
            view.clipChildren = clipChildren
            view.clipToPadding = clipChildren
        }

        handleDefaultRender(baseViewModel, template)

        val borderWidth = baseViewModel.style?.borderWidth
        if (!borderWidth.isNullOrBlank() && getView() is IBorder) {
            val valueStr = template.getValue(borderWidth).stringValue()
            if (!valueStr.isNullOrBlank()) {
                val value = valueStr.parseValue().floatValue()
                (getView() as IBorder).setStrokeWidth(value)
            }
        }

        val borderRadiusArray = baseViewModel.style?.borderRadiusArray()
        if (borderRadiusArray?.size == 4 && getView() is IBorder) {
            val lt = template.getValue(borderRadiusArray[0]).stringValue() // lt
            val rt = template.getValue(borderRadiusArray[1]).stringValue() // rt
            val rb = template.getValue(borderRadiusArray[2]).stringValue() // rb
            val lb = template.getValue(borderRadiusArray[3]).stringValue() // lb

            if (!lt.isNullOrBlank() && !rt.isNullOrBlank() && !rb.isNullOrBlank() && !lb.isNullOrBlank()) {
                (getView() as? IBorder)?.setRadii(
                    lt.parseValue().floatValue(),
                    rt.parseValue().floatValue(),
                    rb.parseValue().floatValue(),
                    lb.parseValue().floatValue())
            } else {
                (getView() as? IBorder)?.setRadius(0f)
            }
        } else {
            val borderRadius = baseViewModel.style?.borderRadius
            if (!borderRadius.isNullOrBlank() && getView() is IBorder) {
                val valueStr = template.getValue(borderRadius).stringValue()
                if (!valueStr.isNullOrBlank()) {
                    val value = valueStr.parseValue().floatValue()
                    (getView() as? IBorder)?.setRadius(value)
                }
            } else {
                (getView() as? IBorder)?.setRadius(0f)
            }
        }



        val borderStyle = baseViewModel.style?.borderStyle
        if (!borderStyle.isNullOrBlank()) {
            val valueStr = template.getValue(borderStyle).stringValue()
            if (valueStr == "dashed" && getView() is IBorder) {
                (getView() as IBorder).setDash(true)
            }
        }

        (getView() as? IBorder)?.setPressedStateChangedListener(this)
    }

    override fun onPressedChanged(pressed: Boolean) {
        val template = template ?: return

        val viewModel = template.getViewModel()

        viewModel.activeStyle ?: return

        if (pressed)
            handlePressedRender(viewModel, template)
        else
            handleDefaultRender(viewModel, template)
    }

    protected open fun handlePressedRender(viewModel: BaseViewModel, template: Template) {
        val activeStyleViewModel = viewModel.activeStyle ?: return

        val background = activeStyleViewModel.background
        if (!background.isNullOrBlank()) {
            val colorStr = template.getValue(background).stringValue()
            if (!colorStr.isNullOrBlank()) {
                val gradientModel = GradientParser.parseGradient(colorStr)
                if (gradientModel != null) {
                    getView().background = gradientModel.getGradientDrawable()
                } else {
                    getView().setBackgroundColor(ColorParser.parseColor(colorStr))
                }
            }
        }

        val opacity = activeStyleViewModel.opacity
        if (!opacity.isNullOrBlank()) {
            val value = template.getValue(opacity).floatValue() ?: 1.0f
            getView().alpha = if (value in 0f..1.0f) value else 1.0f
        }

        val borderColor = activeStyleViewModel.borderColor
        if (!borderColor.isNullOrBlank() && getView() is IBorder) {
            val colorStr = template.getValue(borderColor).stringValue()
            if (!colorStr.isNullOrBlank()) {
                (getView() as? IBorder)?.setStrokeColor(ColorParser.parseColor(colorStr))
            }
        }
    }

    protected open fun handleDefaultRender(viewModel: BaseViewModel, template: Template) {
        val styleViewModel = viewModel.style ?: return

        val background = styleViewModel.background
        if (!background.isNullOrBlank()) {
            val colorStr = template.getValue(background).stringValue()
            if (!colorStr.isNullOrBlank()) {
                val gradientModel = GradientParser.parseGradient(colorStr)
                if (gradientModel != null) {
                    getView().background = gradientModel.getGradientDrawable()
                } else {
                    getView().setBackgroundColor(ColorParser.parseColor(colorStr))
                }
            } else {
                getView().setBackgroundColor(Color.TRANSPARENT)
            }
        } else {
            getView().setBackgroundColor(Color.TRANSPARENT)
        }

        val opacity = styleViewModel.opacity
        if (!opacity.isNullOrBlank()) {
            val value = template.getValue(opacity).floatValue() ?: 1.0f
            getView().alpha = if (value in 0f..1.0f) value else 1.0f
        } else {
            getView().alpha = 1.0f
        }

        val borderColor = styleViewModel.borderColor
        if (!borderColor.isNullOrBlank() && getView() is IBorder) {
            val colorStr = template.getValue(borderColor).stringValue()
            if (!colorStr.isNullOrBlank()) {
                (getView() as? IBorder)?.setStrokeColor(ColorParser.parseColor(colorStr))
            } else {
                (getView() as? IBorder)?.setStrokeColor(0)
            }
        } else {
            (getView() as? IBorder)?.setStrokeColor(0)
        }
    }

    private fun renderAction(baseViewModel: BaseViewModel, template: Template) {

        val url = baseViewModel.action?.click?.url
        val clickUrl = template.getValue(url).stringValue()

        if (clickUrl.isNullOrBlank() && baseViewModel.action?.clickEvent.isNullOrBlank()) { // 无效的 action
            getView().setOnClickListener(null)
            getView().isClickable = false
            return
        }

        getView().isClickable = true
        getView().setOnClickListener {
            if (UIUtils.isFastClick()) return@setOnClickListener // 过滤快速点击行为

            var hasHandle = false // 标记是否有地方消费点击行为，跳转事件和 http 行为互斥

            tryCatch { // 优先执行 click
                hasHandle = hasHandle || handleActionClick(baseViewModel, template)
            }

            tryCatch { // 处理js logic
                hasHandle = hasHandle || handleJSLogicScript(baseViewModel.action?.clickEvent, template)
            }

            if (hasHandle) {
                tryCatch {
                    handleReport(baseViewModel, template)
                }
            }
        }
    }


    private fun handleReport(baseViewModel: BaseViewModel, template: Template) {
        val click = baseViewModel.action?.click ?: return
        // 点击埋点
        val report = click.report ?: return
        template.scanReport()
        val busInfo = template.getDynamicValue(report.busInfo).mapValue() as? Map<String, Any?>?
        MagicCube.starter.reportEvent?.trackClick(busInfo)
    }

    private fun handleActionClick(baseViewModel: BaseViewModel, template: Template): Boolean {
        if (!baseViewModel.hasValidClickUrl()) return false

        // 扫描action
//        template.scanClick()

        val click = baseViewModel.action?.click ?: return false

        val clickUrl = template.getValue(click.url ?: "").stringValue() ?: ""

        if (clickUrl.isBlank() || "null" == clickUrl) return false

        this.getMagicConfig()?.onCardClickListener?.onCardClick(this as BaseWidget<View>, clickUrl)
        return true
    }

    protected fun handleJSLogicScript(jsFuncDesc: String?, template: Template): Boolean {
        if (!Grammar.isFunction(jsFuncDesc)) return false

        try {
            val descriptor = template.scanFunction(jsFuncDesc) ?: return false
            template.getJsEngine()?.asyncExecVoidFunc(descriptor)
            return true
        } catch (t: Throwable) {
            t.printStackTrace()
        }
        return false
    }

    // 曝光
    override fun traceShow(template: Template, baseViewModel: BaseViewModel) {
        val report = baseViewModel.action?.expose?.report ?: return
        tryCatch {
            val busInfo = template.getDynamicValue(report.busInfo).mapValue() as? Map<String, Any?>?
            MagicCube.starter?.reportEvent?.trackExpose(busInfo)
        }
    }
}