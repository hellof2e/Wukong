package com.hellobike.magiccube.loader

import android.content.Context
import com.hellobike.magiccube.loader.insert.IWKInsertLoader
import com.hellobike.magiccube.loader.insert.IWKInsertLoaderListener
import com.hellobike.magiccube.loader.insert.WKInsertLoaderImpl

object WKLoaderCenter {

    fun createInsertLoader(
        context: Context,
        listener: IWKInsertLoaderListener? = null
    ): IWKInsertLoader {
        return WKInsertLoaderImpl(context, listener)
    }
}