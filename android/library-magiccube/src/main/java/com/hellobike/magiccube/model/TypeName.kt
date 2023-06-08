package com.hellobike.magiccube.model


/**
 *
 * @Description:     自定义模版Type对应样式中type字段
 *                   规则为"${业务线标识}-${对应模板名}"
 * @Author:         nikozxh
 * @CreateDate:     2022/1/17 2:30 PM
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TypeName(val name: String)