package com.hellobike.magiccube.model

import android.os.Parcel
import android.os.Parcelable
import kotlin.reflect.KClass

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/1/29 10:47 AM
 */
class PropertyModel{
    var modelFieldName:String? = null
    var propertyType:PropertyType = PropertyType.UNKNOWN
    var propertyClass:KClass<*>? = null
    enum class PropertyType{
        IntProperty,
        StringProperty,
        BooleanProperty,
        RuleProperty,
        EnumProperty,
        MapProperty,
        UNKNOWN
    }
}