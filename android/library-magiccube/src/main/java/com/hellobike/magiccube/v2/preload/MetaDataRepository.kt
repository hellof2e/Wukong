package com.hellobike.magiccube.v2.preload

import android.util.LruCache

class MetaDataRepository {

    private val lru = LruCache<String, IMetaData>(10)

    fun saveValue(key: String, metaData: IMetaData) {
        synchronized(lru) {
            lru.put(key, metaData)
        }
    }

    fun hasValue(key: String): Boolean = getValue(key) != null

    fun getValue(key: String): IMetaData? {
        synchronized(lru) {
            return lru.get(key)
        }
    }

    fun removeKey(key: String): IMetaData? {
        synchronized(lru) {
            return lru.remove(key)
        }
    }
}