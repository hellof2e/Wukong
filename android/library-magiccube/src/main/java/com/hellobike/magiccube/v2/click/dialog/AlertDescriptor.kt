package com.hellobike.magiccube.v2.click.dialog

import com.hellobike.magiccube.v2.js.wrapper.IWKJSObject

class AlertDescriptor {

    var title: String = ""

    var content: String = ""

    var buttons: List<ButtonDescriptor>? = null


    var listener: IOnButtonClickListener? = null

    companion object {
        fun fromJSObject(jsObject: IWKJSObject): AlertDescriptor {

            val alertDescriptor = AlertDescriptor()

            if (jsObject.isNull() || jsObject.isUndefined()) return alertDescriptor

            alertDescriptor.title = jsObject.getString("title") ?: ""
            alertDescriptor.content = jsObject.getString("content") ?: ""

            val buttons = jsObject.getArray("buttons")
            if (buttons != null && !buttons.isNull() && !buttons.isUndefined()) {
                val list = ArrayList<ButtonDescriptor>()
                for (index in 0 until buttons.length()) {
                    val button = buttons.getJSObject(index) ?: continue
                    list.add(ButtonDescriptor.fromJSObject(button))
                }
                alertDescriptor.buttons = list
            }
            return alertDescriptor
        }
    }


    open class ButtonDescriptor {
        var text: String = ""


        companion object {
            fun fromJSObject(jsObject: IWKJSObject): ButtonDescriptor {
                val button = ButtonDescriptor()
                if (jsObject.isNull() || jsObject.isUndefined()) return button

                button.text = jsObject.getString("text") ?: ""
                return button
            }
        }
    }


    interface IOnButtonClickListener {
        fun onClick(buttonIndex: Int, buttonDesc: ButtonDescriptor)
    }
}