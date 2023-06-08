package com.hellobike.magiccube.parser.engine

import com.facebook.yoga.*
import com.google.android.flexbox.*
import com.hellobike.magiccube.model.contractmodel.configs.MagicPosition

object YogaFlexConfigs {

    val POSITION = mapOf(
            Pair(MagicPosition.ABSOLUTE, YogaPositionType.ABSOLUTE),
            Pair(MagicPosition.RELATIVE, YogaPositionType.RELATIVE)
    )

    val FLEX_DIRECTION = mapOf(
            Pair(FlexDirection.ROW, YogaFlexDirection.ROW),
            Pair(FlexDirection.ROW_REVERSE, YogaFlexDirection.ROW_REVERSE),
            Pair(FlexDirection.COLUMN, YogaFlexDirection.COLUMN),
            Pair(FlexDirection.COLUMN_REVERSE, YogaFlexDirection.COLUMN_REVERSE)
    )

    val FLEX_WRAP = mapOf(
            Pair(FlexWrap.NOWRAP, YogaWrap.NO_WRAP),
            Pair(FlexWrap.WRAP, YogaWrap.WRAP),
            Pair(FlexWrap.WRAP_REVERSE, YogaWrap.WRAP_REVERSE)
    )

    val JUSTIFY_CONTENT = mapOf(
            Pair(JustifyContent.FLEX_START, YogaJustify.FLEX_START),
            Pair(JustifyContent.FLEX_END, YogaJustify.FLEX_END),
            Pair(JustifyContent.CENTER, YogaJustify.CENTER),
            Pair(JustifyContent.SPACE_BETWEEN, YogaJustify.SPACE_BETWEEN),
            Pair(JustifyContent.SPACE_AROUND, YogaJustify.SPACE_AROUND),
            Pair(JustifyContent.SPACE_EVENLY, YogaJustify.SPACE_EVENLY)
    )

    val ALIGN_ITEMS = mapOf(
            Pair(AlignItems.FLEX_START, YogaAlign.FLEX_START),
            Pair(AlignItems.FLEX_END, YogaAlign.FLEX_END),
            Pair(AlignItems.CENTER, YogaAlign.CENTER),
            Pair(AlignItems.BASELINE, YogaAlign.BASELINE),
            Pair(AlignItems.STRETCH, YogaAlign.STRETCH)
    )

    val ALIGN_CONTENT = mapOf(
            Pair(AlignContent.FLEX_START, YogaAlign.FLEX_START),
            Pair(AlignContent.FLEX_END, YogaAlign.FLEX_END),
            Pair(AlignContent.CENTER, YogaAlign.CENTER),
            Pair(AlignContent.SPACE_BETWEEN, YogaAlign.SPACE_BETWEEN),
            Pair(AlignContent.SPACE_AROUND, YogaAlign.SPACE_AROUND),
            Pair(AlignContent.STRETCH, YogaAlign.STRETCH)
    )

    val ALIGN_SELF = mapOf(
            Pair(AlignSelf.AUTO, YogaAlign.AUTO),
            Pair(AlignSelf.FLEX_START, YogaAlign.FLEX_START),
            Pair(AlignSelf.FLEX_END, YogaAlign.FLEX_END),
            Pair(AlignSelf.CENTER, YogaAlign.CENTER),
            Pair(AlignSelf.BASELINE, YogaAlign.BASELINE),
            Pair(AlignSelf.STRETCH, YogaAlign.STRETCH)
    )
}