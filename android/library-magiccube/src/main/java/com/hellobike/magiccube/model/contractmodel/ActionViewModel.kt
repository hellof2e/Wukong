package com.hellobike.magiccube.model.contractmodel


/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/9 11:19 AM
 */
//class ActionViewModel : BaseViewModel() {
class ActionViewModel {

    var clickEvent: String? = null
    var countingFinishEvent: String? = null


    var click: ClickActionModel? = null

    var expose: ExposeActionModel? = null

    /**
     * 是否是有效的点击事件url
     */
    fun hasValidClickUrl(): Boolean = click?.hasValidClickUrl() ?: false
}