package com.hellobike.magiccube.model.contractmodel

import com.google.android.flexbox.*
import com.hellobike.magiccube.model.TargetParser
import com.hellobike.magiccube.model.contractmodel.configs.HGravity
import com.hellobike.magiccube.model.contractmodel.configs.Position
import com.hellobike.magiccube.model.property.BooleanProperty
import com.hellobike.magiccube.model.property.EnumProperty
import com.hellobike.magiccube.model.property.IntProperty
import com.hellobike.magiccube.model.property.StringProperty
import com.hellobike.magiccube.parser.LayoutParser

/**
 *
 * @Description:     java类作用描述
 * @Author:         nikozxh
 * @CreateDate:     2021/2/3 5:04 PM
 */
@TargetParser(LayoutParser::class)
open class LayoutViewModel:BaseViewModel() {

    @StringProperty
    var activeBackgroundImage: String? = null
    @StringProperty
    var backgroundImage:String? = null
    @EnumProperty(FlexDirection::class)
    var flexDirection:Int?  =null
    @EnumProperty(JustifyContent::class)
    var justifyContent:Int? =null
    @EnumProperty(AlignContent::class)
    var alignContent:Int? =null
    @EnumProperty(AlignItems::class)
    var alignItems:Int?  =null
    @EnumProperty(AlignSelf::class)
    var alignSelf:Int? =null
    //flex-wrap
    @EnumProperty(FlexWrap::class)
    var flexWrap:Int? =null
    @StringProperty
    var flex:String?  = null
    @StringProperty
    var flexShrink:String?  = null
    @StringProperty
    var flexGrow:String?  = null
    @StringProperty
    var width:String? = null
    @StringProperty
    var height:String? = null
    @StringProperty
    var minWidth:String? = null
    @StringProperty
    var minHeight:String? = null
    @StringProperty
    var maxWidth:String? = null
    @StringProperty
    var maxHeight:String? = null
    @StringProperty
    var marginLeft:String? = null
    @StringProperty
    var marginRight:String? = null
    @StringProperty
    var marginTop:String? = null
    @StringProperty
    var marginBottom:String? = null
    @StringProperty
    var marginHorizontal:String? = null
    @StringProperty
    var marginVertical:String? = null
    @StringProperty
    var paddingLeft:String? = null
    @StringProperty
    var paddingRight:String? = null
    @StringProperty
    var paddingTop:String? = null
    @StringProperty
    var paddingBottom:String? = null
    @StringProperty
    var paddingHorizontal:String? = null
    @StringProperty
    var paddingVertical:String? = null

    @Position
    @StringProperty
    var position: String? = null
    @StringProperty
    var top: String? = null
    @StringProperty
    var right: String? = null
    @StringProperty
    var bottom: String? = null
    @StringProperty
    var left: String? = null

    @StringProperty
    var aspectRatio: String? = null

    @EnumProperty(HGravity::class)
    var textAlign: Int? = null

    var childList:ArrayList<BaseViewModel>? = null


    @BooleanProperty
    var lifecycle: Boolean = false // 是否启用 js 生命周期钩子函数，默认 false


    @BooleanProperty
    var clipChildren: Boolean = true // 是否裁切子元素，默认裁切

    // ==================================== 倒计时特有 ==========================================
    @StringProperty
    var clockOffset: String? = null // 本地时间戳与服务器时间戳偏移量  小于0表示本地时间靠前，大于0表示本地时间靠后

    @StringProperty
    var deadline: String? = null // 结束时间戳

    // 单位是秒
    @StringProperty
    var interval: String? = null // 倒计时间隔时间，浮点类型字符串，单位秒，支持最多1位小数点，默认为1s

    @StringProperty
    var step: String? = null // 每interval时间，倒计时减少值。默认1

    @StringProperty
    var stop: String? = null // 倒计时停止值，默认0

    // time：时间类型倒计时，可显示日，分，秒
    // datetime：时间类型倒计时，可显示天，日，分，秒
    @StringProperty
    var countingType: String? = null // 倒计时类型，默认为time。






    // ==================================== list-view 特有 ==========================================

    @StringProperty
    var listData: String? = null // 用于展示在滚动列表中的数据源（动态值）。

    @StringProperty
    var orientation: String? = null // 滚动的方向（静态值）vertical：垂直滚动 horizontal：水平滚动 默认 horizontal。

    @StringProperty
    var itemKey: String? = null // 在滚动列表中，将根据 key 来进行样式的复用于回收。

    @StringProperty
    var itemType: String? = null // 样式类型静态值（必填）。 在滚动列表中，将根据 item-type 来判断选择使用哪一种布局来进行渲染。


    /**
     * 是否启用 js 生命周期钩子函数
     */
    fun enableJSLifecycle() = lifecycle
}