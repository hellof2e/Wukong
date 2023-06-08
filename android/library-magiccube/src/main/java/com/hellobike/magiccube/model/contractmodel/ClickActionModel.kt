package com.hellobike.magiccube.model.contractmodel

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/9 11:28 AM
 */
class ClickActionModel {
    var url: String? = null

    var report: ReportModel? = null

    /**
     * 是否是有效的点击url
     */
    fun hasValidClickUrl(): Boolean = !url.isNullOrBlank()

}