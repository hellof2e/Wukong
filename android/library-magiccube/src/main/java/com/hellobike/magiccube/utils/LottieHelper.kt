package com.hellobike.magiccube.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.hellobike.magiccube.net.CoroutineSupport
import com.hellobike.magiccube.parser.DSLParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object LottieHelper {


    @JvmStatic
    fun loadLottie(context: Context, url: String, callback: ((lottieCompose:LottieComposition?) -> Unit)) {
        if (context is Activity) {
            if (DSLParser.isDestroy(context)) return
        }
        CoroutineSupport().launch(Dispatchers.IO) {
            try {
                val lottieCompose = loadLottieCompose(context, url) ?: return@launch
                withContext(Dispatchers.Main) {
                    if (context is Activity) {
                        if (DSLParser.isDestroy(context)) return@withContext
                    }
                    callback.invoke(lottieCompose)
                }
            }catch (e:Exception){
                withContext(Dispatchers.Main) {
                    if (context is Activity) {
                        if (DSLParser.isDestroy(context)) return@withContext
                    }
                    callback.invoke(null)
                }
            }
        }
    }


    @JvmStatic
    suspend inline fun loadLottieCompose(context: Context, url: String): LottieComposition? {
        return suspendCancellableCoroutine { continuation ->
            var resumed = false
            val task = LottieCompositionFactory.fromUrl(context, url, getCacheKey(url))
            task.addListener {
                if (!resumed && continuation.isActive) {
                    continuation.resume(it)
                    resumed = true
                }
            }
            task.addFailureListener {
                if (!resumed && continuation.isActive) {
                    continuation.resumeWithException(it)
                    resumed = true
                }
            }
        }
    }

    @JvmStatic
    fun getCacheKey(url: String): String {
        val uri = Uri.parse(url)
        val cacheKeyBuilder = Uri.Builder()
        cacheKeyBuilder.scheme(uri.scheme).authority(uri.authority).path(uri.path)
        for (key in uri.queryParameterNames) {
            if (TextUtils.equals("Expires", key) || TextUtils.equals("Signature", key)
                || TextUtils.equals("OSSAccessKeyId", key)
            ) {
                continue
            }
            cacheKeyBuilder.appendQueryParameter(key, uri.getQueryParameter(key))
        }
        val cacheKey = cacheKeyBuilder.build().toString()
        return cacheKey
    }

}