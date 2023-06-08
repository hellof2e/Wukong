package com.hellobike.magiccube.model.contractmodel

import android.view.View
import com.hellobike.magiccube.model.property.BooleanProperty
import com.hellobike.magiccube.model.property.ListProperty
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.v2.configs.Constants
import java.io.Serializable

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/3 4:57 PM
 */
open class BaseViewModel:Serializable {
    @StringProperty
    var nodeId: String? = null

    var type: String? = null
    var layout:LayoutViewModel? = null
    var style:StyleViewModel? = null
    var action:ActionViewModel? = null
    var activeStyle:ActiveStyleViewModel? = null

    @ListProperty
    var mFor:String? = null
    @StringProperty
    var mIf:String? = null
    @StringProperty
    var engine: String? = null // 引擎 (yoga, flex, none)

    var logic: String? = null // 逻辑

    var scopePrefixName: String? = null
        get() { return if (hasFor() || type == Constants.NODE_LIST_VIEW) "listItem" else null }

    var scopeIndexPrefixName: String? = null
        get() { return if (hasFor() || type == Constants.NODE_LIST_VIEW) "listItemIndex" else null }


    fun hasIf() = !mIf.isNullOrBlank()

    fun hasFor() = !mFor.isNullOrBlank()


    /**
     * 是否是有效的点击事件url
     */
    fun hasValidClickUrl(): Boolean = action?.hasValidClickUrl() ?: false
}