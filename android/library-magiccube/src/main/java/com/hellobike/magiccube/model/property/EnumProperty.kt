package com.hellobike.magiccube.model.property

import androidx.annotation.RestrictTo
import kotlin.reflect.KClass

/**
 *
 * @Description:     String类型属性
 * @Author:         nikozxh
 * @CreateDate:     2021/1/28 1:54 PM
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnumProperty(val enumName:KClass<*>)