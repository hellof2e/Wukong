package com.hellobike.magiccube.parser


import com.alibaba.fastjson.JSONObject
import com.hellobike.magiccube.model.PropertyModel
import com.hellobike.magiccube.model.contractmodel.LayoutViewModel
import com.hellobike.magiccube.model.hump2strike
import com.hellobike.magiccube.model.scanProperty
import java.lang.Exception

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/1/28 2:43 PM
 */
abstract class BaseParser<T> {
    companion object{
        var parserMap:HashMap<BaseParser<*>,HashMap<String,PropertyModel>> = HashMap()
    }

    abstract fun parseElement(json: JSONObject):T
    fun fillModel(model:Any, ob: JSONObject, propMap:HashMap<String,PropertyModel>){
        for( attr in propMap!!.entries){
            try{
                val cssKey = attr.key.hump2strike()
                if(ob.contains(cssKey)){
                    val attrVal = ob.getString(cssKey)
                    val field = model.javaClass.getDeclaredField(attr.key)
                    field.isAccessible = true
                    when(attr.value.propertyType){
                        PropertyModel.PropertyType.StringProperty->
                            field.set(model,attrVal)
                        PropertyModel.PropertyType.IntProperty->
                            field.set(model,attrVal.toInt())
                        PropertyModel.PropertyType.BooleanProperty->
                            field.set(model,attrVal.toBoolean())
                        PropertyModel.PropertyType.EnumProperty->{
                            //Enum类谷歌都实现为一个接口并且将对应值转为大写常量
                            val constVal = attrVal.replace("-","_").toUpperCase()
                            //抽取属性值映射至对象
                            val enum = attr.value.propertyClass?.java?.getDeclaredField(constVal)
                            val enumVal = enum!!.getInt(attr.value.propertyClass!!.java)
                            field.set(model,enumVal)
                        }
                        PropertyModel.PropertyType.MapProperty->{
                            if(attrVal.startsWith("\${")){
                                field.set(model,attrVal)
                            }
                            else{
                                field.set(model,JSONObject.parseObject(attrVal,HashMap::class.java))
                            }
                        }
//                        PropertyModel.PropertyType.UNKNOWN->
//                            field.set(model,attrVal.toString())
                    }
                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}