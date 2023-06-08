package com.hellobike.magiccube.parser.engine

import android.content.Context

object ViewEngineManager {

    fun createViewEngine(context: Context, engine: Engine?): IViewEngine  {
        YogaEngine.initEngine(context)
        return YogaFlexViewEngine()
    }
}