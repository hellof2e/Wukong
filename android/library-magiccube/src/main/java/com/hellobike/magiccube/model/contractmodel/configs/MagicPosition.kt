package com.hellobike.magiccube.model.contractmodel.configs

import androidx.annotation.StringDef


object MagicPosition {
    const val RELATIVE = "relative"
    const val ABSOLUTE = "absolute"

    fun check(@Position position: String?): Boolean {
        return position != null && (position == RELATIVE || position == ABSOLUTE)
    }
}

@StringDef(MagicPosition.RELATIVE, MagicPosition.ABSOLUTE)
@Retention(AnnotationRetention.SOURCE)
annotation class Position

