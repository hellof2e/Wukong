package com.hellobike.magiccube.loader.insert

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.InsetDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import com.hellobike.library_magiccube.R
import com.hellobike.magiccube.loader.WKLoaderParam
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.widget.MagicBoxLayout

class WKDialog(context: Context, private val params: WKLoaderParam, themeResId: Int = 0) :
    AlertDialog(context, themeResId) {

    companion object {
        fun newInstance(view: MagicBoxLayout, params: WKLoaderParam): WKDialog {
            val dialog = WKDialog(view.context, params, R.style.MagicBox_CustomerDialogStyle)
            dialog.magicBoxLayout = view
            return dialog
        }
    }

    private lateinit var magicBoxLayout: MagicBoxLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        magicBoxLayout.gravity = params.gravity
        magicBoxLayout.fitsSystemWindows = true
        setContentView(magicBoxLayout)

        showHistoryAnimation()
    }

    private fun showHistoryAnimation() {

        val child = magicBoxLayout.getChildAt(0)
        val width =
            if (child == null || child.layoutParams.width != ViewGroup.LayoutParams.MATCH_PARENT)
                ViewGroup.LayoutParams.WRAP_CONTENT
            else ViewGroup.LayoutParams.MATCH_PARENT


//        (magicBoxLayout.layoutParams as? ViewGroup.MarginLayoutParams)?.apply {
//            bottomMargin = -500
//        }
        this.setCanceledOnTouchOutside(params.canceledOnTouchOutside)
        this.window?.apply {
            setGravity(params.gravity)
            val lp = this.attributes
            lp.dimAmount = params.dimAmount
            lp.horizontalMargin = 0f
//            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
            lp.width = width
            attributes = lp
            addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
//            if (params.gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
//                setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
//            }
            clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
            setBackgroundDrawable(InsetDrawable(ColorDrawable(Color.TRANSPARENT), 0))

        }
    }

    override fun show() {
        if (!UIUtils.contextIsValid(context)) return
        tryCatch {
            super.show()
        }
    }
}