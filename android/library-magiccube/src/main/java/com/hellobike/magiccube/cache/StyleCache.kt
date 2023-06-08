package com.hellobike.magiccube.cache

import android.content.Context
import android.util.LruCache
import com.hellobike.magiccube.model.StyleModel
import com.hellobike.magiccube.utils.EncryptUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class StyleCache(val context: Context) {
    private val DEFAULT_DISK_CACHE_SIZE = 20 * 1024 * 1024L
    private val DEFAULT_MEMERY_SIZE = 30
    private val DISK_PATH = "dsl_cache"
    private val lruCache: LruCache<String, String> = LruCache(DEFAULT_MEMERY_SIZE)
    var diskLruCache: DiskLruCache? = null

    private val commitURL = ArrayList<String>()

    fun initWithDiskPath(path: String?) {
        try {
            val cacheDir = context.cacheDir
//            val cacheDir = context.externalCacheDir
            if (cacheDir != null) {
                val result = File(cacheDir, if (path.isNullOrBlank()) DISK_PATH else path)
                val p1 = context.packageManager.getPackageInfo(context.packageName, 0)
                diskLruCache = DiskLruCache.open(result, 627, 1, DEFAULT_DISK_CACHE_SIZE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun initLruCache() {

    }

    @Synchronized
    private fun resetDiskCache() {
        diskLruCache = null
        synchronized(lruCache) {
            lruCache.evictAll()
        }
    }

    private fun saveToDisk(key: String, content: String) {
        synchronized(commitURL) {
            if (commitURL.contains(key)) {
                return
            }
            commitURL.add(key)
        }
        GlobalScope.launch(Dispatchers.IO) {
            var editor: DiskLruCache.Editor? = null
            try {
                editor = diskLruCache?.edit(key)
                editor?.set(0, content)
                editor?.commit()
            } catch (e: Exception) {
                e.printStackTrace()
                try {
                    editor?.abort()
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
            } finally {
                synchronized(commitURL) {
                    commitURL.remove(key)
                }
            }
        }
    }

    fun cleanWithUrl(url: String?) {
        if (url.isNullOrBlank()) return
        val safeKey = getSafeKey(url)
        try {
            diskLruCache?.remove(safeKey)
            synchronized(lruCache) {
                lruCache.remove(safeKey)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Synchronized
    fun clearAll() {
        try {
            diskLruCache?.delete()
            resetDiskCache()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getSafeKey(cacheKey: String?): String? {
        return EncryptUtils.encryptMD5ToString(cacheKey)
    }

    fun storeStyle(style: StyleModel, url: String) {
        val safeKey = getSafeKey(url)
        if (safeKey == null || style.styleString.isNullOrBlank())
            return

        if (!style.styleString.isNullOrBlank()) {
            saveToMemory(safeKey, style.styleString) // 写内存
            saveToDisk(safeKey, style.styleString!!) // 写磁盘
        }
    }

    fun queryStyleWithUrl(url: String): String? {
        val safeKey = getSafeKey(url)
        if (safeKey.isNullOrBlank()) return null
//        logd("url:$url, md5值：$safeKey, DSL开始查内存")
        val opt = queryFromMemory(safeKey)
        if (!opt.isNullOrBlank()) {
//            logd("[命中内存缓存]")
            return opt
        }
//        logd("内存缓存没有命中! DSL开始查磁盘缓存")
        val queryFromDisk = queryFromDisk(safeKey)
        if (!queryFromDisk.isNullOrBlank()) { // 内存中放一份
            saveToMemory(safeKey, queryFromDisk)
//            logd("[命中磁盘缓存]")
        } else {
//            logd("磁盘缓存没有命中!")
        }
        return queryFromDisk
    }

    fun queryFromMemory(safeKey: String): String? = try {
        synchronized(lruCache) {
            return lruCache.get(safeKey)
        }
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

    internal fun injectToMemory(url: String, content: String?) {
        val safeKey = getSafeKey(url) ?: return
        if (content == null) return
        saveToMemory(safeKey, content)
    }

    private fun saveToMemory(safeKey: String, content: String?) {
        if (content.isNullOrBlank()) return
        try {
            synchronized(lruCache) {
                lruCache.put(safeKey, content)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    private fun queryFromDisk(safeKey: String): String? {
        return try {
            diskLruCache?.get(safeKey)?.getString(0)
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }
}