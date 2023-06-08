package com.hellobike.magiccube.widget

import android.view.View

interface IParent {
    fun markDirty(view: View)
}