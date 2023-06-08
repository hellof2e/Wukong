package com.hellobike.magiccube.parser.engine

import android.graphics.drawable.GradientDrawable
import com.hellobike.magiccube.model.MagicValue
import com.hellobike.magiccube.model.property.EnumProperty
import com.hellobike.magiccube.model.property.IntProperty
import com.hellobike.magiccube.model.property.ListProperty
import com.hellobike.magiccube.parser.DSLParser.parseValue

object GradientParser {
    class GradientModel {
        @ListProperty
        var colors: IntArray? = null

        @IntProperty
        var gradientType = GradientDrawable.LINEAR_GRADIENT

        @EnumProperty(GradientDrawable.Orientation::class)
        var orientation  = GradientDrawable.Orientation.BOTTOM_TOP

        @ListProperty
        var locations: FloatArray? = null


        fun getGradientDrawable() : GradientDrawable {
            val gradient = GradientDrawable()
            gradient.gradientType = gradientType
            gradient.orientation  = orientation
            colors?.let { colorsIt ->
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
                    && locations?.count() == colorsIt.count()) {
                    gradient.setColors(colorsIt, locations)
                } else {
                    gradient.colors = colorsIt
                }
            }
            return gradient
        }
    }

    fun parseGradient(gradientString:String?) : GradientModel? {
        var gradientModel: GradientModel? = null
        if (gradientString.isNullOrBlank()) {
            return gradientModel
        }

        if (gradientString.startsWith("linear-gradient(")) {
            val colors = gradientString.replace("linear-gradient(", "")
                .replace(")", "").trim()
                .split(",")

            if (colors.count() >= 2) {
                gradientModel = GradientModel()
                gradientModel.gradientType = GradientDrawable.LINEAR_GRADIENT

                //方向
                gradientModel.orientation = parseDirection(colors.first().trim())

                //colors and locations
                parseColorsAndLocations(colors.subList(1, colors.size), gradientModel)
            }
        }
        return gradientModel
    }

    private fun parseColorsAndLocations(colorParagraphs:List<String>, gradientModel:GradientModel) {
        val colors = ArrayList<Int>()
        val locations = ArrayList<Float>()
        for (colorString in colorParagraphs) {
            val colorPair = colorString.trim().split(" ")
            val c = ColorParser.parseColor(colorPair.first().trim())
            colors.add(c)
            if (colorPair.count() > 1 ) {
                val loc = colorPair[1].trim().parseValue()
                if (loc != MagicValue.ZERO) {
                    locations.add(loc.floatValue() / 100)
                }
            }
        }
        gradientModel.colors = colors.toIntArray()
        gradientModel.locations = locations.toFloatArray()
    }

    private fun parseDirection(directionString:String): GradientDrawable.Orientation {
        var orientation = GradientDrawable.Orientation.BOTTOM_TOP
        when (directionString) {
            "to left"         -> orientation = GradientDrawable.Orientation.RIGHT_LEFT
            "to right"        -> orientation = GradientDrawable.Orientation.LEFT_RIGHT
            "to bottom"       -> orientation = GradientDrawable.Orientation.TOP_BOTTOM
            "to top"          -> orientation = GradientDrawable.Orientation.BOTTOM_TOP
            "to right bottom" -> orientation = GradientDrawable.Orientation.TL_BR
            "to right top"    -> orientation = GradientDrawable.Orientation.BL_TR
            "to left top"     -> orientation = GradientDrawable.Orientation.BR_TL
            "to left bottom"  -> orientation = GradientDrawable.Orientation.TR_BL
        }

        return orientation
    }
}



