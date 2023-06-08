package com.hellobike.magiccube.v2

import android.util.LruCache
import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.utils.tryCatch

internal object StyleModelCache {

    private val cardStyleModels = LruCache<String, StyleModel>(30)

    fun hasValidStyleModel(url: String): Boolean =
        getStyleModelFromCache(url)?.isValidViewModel() ?: false

    fun getStyleModelFromCache(url: String): StyleModel? {
        synchronized(cardStyleModels) {
            try {
                return cardStyleModels.get(url)
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        return null
    }

    fun cacheStyleModel(url: String, model: StyleModel) {
        if (!model.isValidViewModel()) return // 数据不可靠，不保存
        tryCatch {
            synchronized(cardStyleModels) {
                cardStyleModels.put(url, model)
            }
        }
    }


    fun removeStyleModel(url: String) {
        tryCatch {
            synchronized(cardStyleModels) {
                cardStyleModels.remove(url)
            }
        }
    }


}