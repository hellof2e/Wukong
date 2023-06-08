package com.hellobike.magiccube.model

import com.hellobike.magiccube.model.property.*
import com.hellobike.magiccube.parser.BaseParser
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.reflect.KClass


/**
 *
 * @Description:     初始化扫描类内属性注册至BaseParser下属性Map
 * @Author:         nikozxh
 * @CreateDate:     2021/1/28 5:06 PM
 */
fun scanProperty(clazz: KClass<*>, parser:BaseParser<*>){
    try {
            val propertyHashMap = HashMap<String, PropertyModel>()
            for(field in clazz.java.declaredFields){
//                field.isAccessible  = true
                if(field.annotations.size!=0){
                    val model = PropertyModel()
                    model.modelFieldName = field.name
                    if(field.isAnnotationPresent(StringProperty::class.java)){
                        model.propertyType = PropertyModel.PropertyType.StringProperty
                    }
                    else if(field.isAnnotationPresent(IntProperty::class.java)){
                        model.propertyType = PropertyModel.PropertyType.IntProperty
                    }
                    else if(field.isAnnotationPresent(BooleanProperty::class.java)){
                        model.propertyType = PropertyModel.PropertyType.BooleanProperty
                    }
                    else if(field.isAnnotationPresent(EnumProperty::class.java)){
                        model.propertyType = PropertyModel.PropertyType.EnumProperty
                        model.propertyClass = (field.annotations[0] as EnumProperty).enumName
                    }
                    else if(field.isAnnotationPresent(MapProperty::class.java)){
                        model.propertyType = PropertyModel.PropertyType.MapProperty
                    }
                    if(model.propertyType==null){
                        model.propertyType = PropertyModel.PropertyType.UNKNOWN
                    }

                    propertyHashMap.put(field.name, model)
                }
            }
        BaseParser.parserMap.put(parser, propertyHashMap)

    }catch (e: Exception){
        e.printStackTrace()
    }
}

private val linePattern: Pattern = Pattern.compile("[-]")
private val humpPattern: Pattern = Pattern.compile("[A-Z]")

fun String.strike2hump():String{
    val matcher: Matcher = linePattern.matcher(this)
    val sb = StringBuffer()
    while (matcher.find()) {
        matcher.appendReplacement(sb, matcher.group(1).toUpperCase())
    }
    matcher.appendTail(sb)
    return sb.toString()
}

fun String.hump2strike(): String {
    val matcher = humpPattern.matcher(this)
    val sb = StringBuffer()
    while (matcher.find()) {
        matcher.appendReplacement(sb, "-" + matcher.group(0))
    }
    matcher.appendTail(sb)
    return sb.toString().toLowerCase()
}
