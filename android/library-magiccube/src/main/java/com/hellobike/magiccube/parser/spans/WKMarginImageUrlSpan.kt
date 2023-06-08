package com.hellobike.magiccube.parser.spans

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.hellobike.magiccube.parser.widget.TextWidget
import java.lang.ref.WeakReference

class WKMarginImageUrlSpan(
    private val url: String,
    widget: TextWidget?,
    private val width: Int = 0,
    private val height: Int = 0,
    placeHolder: Drawable = ColorDrawable(Color.TRANSPARENT).apply {
        setBounds(
            0,
            0,
            width,
            height
        )
    },
    verticalAlignment: Int = WKAlignMiddleImageSpan.ALIGN_MIDDLE,
    private val marginLeft: Int = 0,
    private val marginRight: Int = 0,
    offsetY: Int = 0
) : WKMarginImageSpan(placeHolder, verticalAlignment, marginLeft, marginRight, offsetY) {

    private val widget: WeakReference<TextWidget> = WeakReference(widget)

    fun load() {

        val w = if (width > 0) width else Target.SIZE_ORIGINAL
        val h = if (height > 0) height else Target.SIZE_ORIGINAL

        val context = widget.get()?.getView()?.context ?: return

        Glide.with(context)
            .load(url)
            .dontTransform()
            .override(w, h)
            .into(object : SimpleTarget<GlideDrawable>() {
                override fun onResourceReady(
                    resource: GlideDrawable?,
                    glideAnimation: GlideAnimation<in GlideDrawable>?
                ) {
                    try {
                        val widget = widget.get() ?: return
                        val view = widget.getView()

                        val span = view.text as? Spannable ?: return

                        if (resource != null) {
                            var intrinsicWidth =
                                if (drawable.bounds.width() <= 0) resource.intrinsicWidth else drawable.bounds.width()
                            var intrinsicHeight =
                                if (drawable.bounds.height() <= 0) resource.intrinsicHeight else drawable.bounds.height()

                            // 修正尺寸
                            // 1. 开发指定的宽高，以开发指定的为准
                            // 2. 开发没有指定，按照原始大小
                            // 3. 开发指定了一个，则按照等比缩放的方式处理
                            if (drawable.bounds.width() > 0 && drawable.bounds.height() > 0) { // 开发指定的宽高，以开发指定的为准
                                intrinsicWidth = drawable.bounds.width()
                                intrinsicHeight = drawable.bounds.height()
                            } else if (drawable.bounds.width() <= 0 && drawable.bounds.height() <= 0) { // 开发没有指定，按照原始大小
                                intrinsicWidth = resource.intrinsicWidth
                                intrinsicHeight = resource.intrinsicHeight
                            } else if (drawable.bounds.width() > 0 && drawable.bounds.height() <= 0) { // 开发指定了一个，则按照等比缩放的方式处理
                                intrinsicWidth = drawable.bounds.width()
                                intrinsicHeight =
                                    (resource.intrinsicHeight * 1.0f * intrinsicWidth / resource.intrinsicWidth).toInt()
                            } else if (drawable.bounds.width() <= 0 && drawable.bounds.height() > 0) { // 开发指定了一个，则按照等比缩放的方式处理
                                intrinsicHeight = drawable.bounds.height()
                                intrinsicWidth =
                                    (resource.intrinsicWidth * intrinsicHeight * 1.0f / resource.intrinsicHeight).toInt()
                            }

                            if (drawable.bounds.width() != intrinsicWidth || drawable.bounds.height() != intrinsicHeight) {
                                widget.markDirty()
                            }


                            val start = span.getSpanStart(this@WKMarginImageUrlSpan)
                            val end = span.getSpanEnd(this@WKMarginImageUrlSpan)
                            span.removeSpan(this@WKMarginImageUrlSpan)
                            resource.setBounds(0, 0, intrinsicWidth, intrinsicHeight)

                            val wkMarginImageSpan =
                                WKMarginImageSpan(
                                    resource,
                                    verticalAlignment,
                                    marginLeft,
                                    marginRight
                                )

                            span.setSpan(
                                wkMarginImageSpan,
                                start,
                                end,
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                            )
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
            })
    }


}