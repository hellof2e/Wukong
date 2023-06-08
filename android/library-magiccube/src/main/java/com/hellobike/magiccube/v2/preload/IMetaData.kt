package com.hellobike.magiccube.v2.preload

import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.v2.js.IJsEngine

interface IMetaData {

    fun getJS(): IJsEngine?

    fun getStyle(): StyleModel?

    fun getCode(): Int

    fun getMsg(): String

    fun hasSuccess(): Boolean

    fun getUrl(): String

    fun getData(): HashMap<String, Any?>
}