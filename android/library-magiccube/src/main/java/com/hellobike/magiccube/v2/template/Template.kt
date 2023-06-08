package com.hellobike.magiccube.v2.template

import com.hellobike.magiccube.model.contractmodel.BaseViewModel

open class Template(private val baseViewModel: BaseViewModel) : BaseTemplate() {

    private var parentTemplate: Template? = null

    internal fun getViewModel(): BaseViewModel = baseViewModel

    // 关联父亲节点的 template
    internal fun bindParentTemplate(template: Template?) {
        this.parentTemplate = template
    }

    internal fun scan() {
        this.hasChanged = false
        doScan()
    }

    open fun doScan() {
        scanStyleViewModel()
        scanActiveStyleViewModel()
        scanFor()
        scanIf()
//        scanAction()
        scanClick()
        scanExpose()
    }

    internal fun scanExpose() {
        val report = baseViewModel.action?.expose?.report ?: return
        val busInfo = report.busInfo
        scanDynamicMapData(busInfo)
    }

    internal fun scanClick() {
        val click = baseViewModel.action?.click ?: return

        if(click.url.isNullOrBlank()) return

        fillTemplate(click.url)
    }

    internal fun scanReport() {
        val click = baseViewModel.action?.click ?: return

        val report = click.report ?: return

        val busInfo = report.busInfo
        scanDynamicMapData(busInfo)
    }

    private fun scanStyleViewModel() {
        val style = baseViewModel.style ?: return
        fillTemplate(style.borderWidth)
        fillTemplate(style.background)
        fillTemplate(style.borderColor)

        val borderRadiusArray = style.borderRadiusArray()
        if (borderRadiusArray.size == 4) {
            borderRadiusArray.forEach { fillTemplate(it) }
        } else {
            fillTemplate(style.borderRadius)
        }

        fillTemplate(style.borderStyle)
        fillTemplate(style.opacity)
    }

    private fun scanActiveStyleViewModel() {
        val style = baseViewModel.activeStyle ?: return
        fillTemplate(style.background)
        fillTemplate(style.borderColor)
        fillTemplate(style.opacity)
    }

    internal fun scanFor() {
        if (baseViewModel.hasFor()) {
            fillTemplate(baseViewModel.mFor)
        }
    }

    internal fun scanIf() {
        if (baseViewModel.hasIf()) {
            fillExpression(baseViewModel.mIf)
        }
    }

    fun isRequiredTraceShow(): Boolean = baseViewModel.action?.click?.report != null
}