package com.hellobike.magiccube.loader.insert

import com.hellobike.magiccube.loader.WKLoaderParam
import com.hellobike.magiccube.utils.UIUtils
import com.hellobike.magiccube.widget.MagicBoxLayout

class WKInsertImpl(private val magicBoxLayout: MagicBoxLayout, private val params: WKLoaderParam) :
    IWKInsert {

    private var wkInsertDialogListener: IWKInsertDialogListener? = null

    private var dialog: WKDialog? = null

    private var closeByUser = false

    override fun setOnInsertDialogListener(listener: IWKInsertDialogListener) {
        wkInsertDialogListener = listener
    }

    override fun process() {
        if (!UIUtils.contextIsValid(magicBoxLayout.context)) return

        val dialog = dialog ?: WKDialog.newInstance(magicBoxLayout, params)

        dialog.setOnShowListener {
            wkInsertDialogListener?.onShowing()
        }
        dialog.setOnDismissListener {
            magicBoxLayout.release()
            wkInsertDialogListener?.onDismiss(closeByUser)
        }

        dialog.show()
        this.dialog = dialog
    }

    override fun isShowing(): Boolean = dialog?.isShowing ?: false

    override fun dismiss() {
        dismiss(false)
    }

    override fun dismiss(byUser: Boolean) {
        closeByUser = byUser
        dialog?.dismiss()
    }
}