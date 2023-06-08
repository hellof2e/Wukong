package com.hellobike.magiccube.model

import androidx.annotation.RestrictTo
import kotlin.reflect.KClass

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/1/28 5:26 PM
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TargetParser (val targetClazz: KClass<*>)