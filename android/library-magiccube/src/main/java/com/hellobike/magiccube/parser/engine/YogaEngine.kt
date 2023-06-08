package com.hellobike.magiccube.parser.engine

import android.content.Context
import com.facebook.soloader.SoLoader

object YogaEngine {

    private const val reloadCount = 3

    private var inited = false

    fun initEngine(context: Context) {
        val application = context.applicationContext
        synchronized(YogaEngine::class.java) {
            initYoga(application)
        }
    }

    private fun initYoga(context: Context, count: Int = 1) {
        if (inited) return
        try {
            SoLoader.init(context, false)
            inited = true
        } catch (t: Throwable) {
            t.printStackTrace()
            if (count <= reloadCount) {
                initYoga(context, count + 1)
            }

            if (count == reloadCount) {
                reportException()
            }
        }
    }

    private fun reportException() {

    }

}