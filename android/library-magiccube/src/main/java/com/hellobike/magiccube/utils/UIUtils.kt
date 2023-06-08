package com.hellobike.magiccube.utils

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import android.os.Looper
import android.provider.MediaStore
import android.view.View
import com.hellobike.magiccube.v2.configs.Constants
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat

object UIUtils {

    private val format = DecimalFormat("00")

    private var lastClickTime: Long = 0

    fun isFastClick(invert: Long = 100): Boolean {
        val previous = System.currentTimeMillis()
        val current: Long = previous - lastClickTime
        if (current in 1 until invert) {
            return true
        }
        lastClickTime = previous
        return false
    }

    fun runOnUiThread(block: () -> Unit) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            block.invoke()
        } else {
            Constants.MAIN_HANDLER.post(block)
        }
    }


    fun decimalFormat00(number: Number): String {
        return format.format(number)
    }

    fun contextIsValid(context: Context?): Boolean {
        if (context == null) return false
        if (context is Activity) {
            if (context.isFinishing) return false
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1 && context.isDestroyed) return false
        }
        return true
    }


    fun viewToBitmap(view: View): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        } catch (t: Throwable) {
            t.printStackTrace()
            bitmap?.recycle()
        }
        return bitmap
    }

    /**
     * 保存bitmap到相册
     */
    fun saveBitmap(context: Context, bitmap: Bitmap?, callback: (Boolean) -> Unit) {
        var fos: FileOutputStream? = null
        try {
            if (bitmap != null) {
                val appDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                    "Camera"
                )
                if (!appDir.exists()) {
                    appDir.mkdir()
                }
                val fileName = System.currentTimeMillis().toString() + ".jpg"
                val file = File(appDir, fileName)
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()

                //通知更新
                context.sendBroadcast(
                    Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(
                            file
                        )
                    )
                )
                callback.invoke(true)
            } else {
                callback.invoke(false)
            }
        } catch (e: Exception) {
            callback.invoke(false)
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (ignored: IOException) {
                }
            }
        }
    }

    fun saveBitmapV2(context: Context, bitmap: Bitmap?, callback: (Boolean) -> Unit) {
        if (bitmap == null) {
            callback.invoke(false)
            return
        }
        var outputStream: OutputStream? = null
        try {
            val cv = ContentValues()
            cv.put(
                MediaStore.Images.Media.DISPLAY_NAME,
                System.currentTimeMillis().toString() + ".jpg"
            )
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            val saveUri: Uri? = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                cv
            )
            if (saveUri == null) {
                callback.invoke(false)
                return
            }
            outputStream = context.contentResolver.openOutputStream(saveUri)
            if (outputStream == null) {
                callback.invoke(false)
                return
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            callback.invoke(true)
        } catch (t: Throwable) {
            t.printStackTrace()
            callback.invoke(false)
        } finally {
            try {
                outputStream?.close()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    // navigation bar
    ///////////////////////////////////////////////////////////////////////////
    fun getNavBarHeight(context: Context): Int {
        val res = context.applicationContext.resources
        val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId != 0) {
            res.getDimensionPixelSize(resourceId)
        } else {
            0
        }
    }

}