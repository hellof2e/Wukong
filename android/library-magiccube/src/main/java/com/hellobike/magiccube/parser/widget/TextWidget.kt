package com.hellobike.magiccube.parser.widget

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import com.hellobike.magiccube.model.contractmodel.BaseViewModel
import com.hellobike.magiccube.model.contractmodel.RichTextModel
import com.hellobike.magiccube.model.contractmodel.SpanViewModel
import com.hellobike.magiccube.model.contractmodel.TextViewModel
import com.hellobike.magiccube.parser.DSLParser
import com.hellobike.magiccube.parser.DSLParser.dimen2px
import com.hellobike.magiccube.parser.DSLParser.parseValue
import com.hellobike.magiccube.parser.engine.ColorParser
import com.hellobike.magiccube.parser.spans.CustomTypefaceSpan
import com.hellobike.magiccube.parser.spans.LineThroughSpan
import com.hellobike.magiccube.parser.spans.WKMarginImageUrlSpan
import com.hellobike.magiccube.utils.tryCatch
import com.hellobike.magiccube.v2.template.Template
import com.hellobike.magiccube.widget.BorderTextView

class TextWidget(context: Context) : BaseWidget<BorderTextView>(context) {

    override fun initView(): BorderTextView = (viewEngine.createTextView(context) as BorderTextView).apply {
        includeFontPadding = false
    }

    private fun updateSpan(content: String, span: SpannableString, startIndex: Int, richTextModel: RichTextModel) {
        if (!richTextModel.color.isNullOrBlank() && richTextModel.color?.startsWith("\${") != true) {
            val color = richTextModel.color
            if (!color.isNullOrBlank()) {
                span.setSpan(
                    ForegroundColorSpan(ColorParser.parseColor(color)),
                    startIndex,
                    startIndex + content.length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
        }
        if (!richTextModel.fontWeight.isNullOrBlank() && (richTextModel.fontWeight!! == "bold" || richTextModel.fontWeight!! == "semibold")) {
            span.setSpan(DSLParser.FakeBoldSpan(), startIndex, startIndex + content.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (!richTextModel.fontSize.isNullOrBlank() && richTextModel.fontSize?.startsWith("\${") != true) {
            span.setSpan(AbsoluteSizeSpan(richTextModel.fontSize!!.dimen2px(), false), startIndex, startIndex + content.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

    }

    override fun render(baseViewModel: BaseViewModel, template: Template) {
        super.render(baseViewModel, template)
        when (baseViewModel) {
            is SpanViewModel -> renderSpan(baseViewModel, template)
            is TextViewModel -> renderText(baseViewModel, template)
        }

    }

    private fun renderText(baseViewModel: TextViewModel, template: Template) {
        val text = baseViewModel.text

        if (text.isNullOrEmpty()) return

        var hasImage = false
        val span = SpannableStringBuilder()
        val imageSpans = ArrayList<WKMarginImageUrlSpan>()
        text.forEach {

            if (!it.content.isNullOrBlank()) {
                val content = template.getValue(it.content!!).stringValue()

                if (!content.isNullOrBlank()) {
                    val contentText = SpannableString(content)

                    if (!it.fontSize.isNullOrBlank()) {
                        val fontSize = template.getValue(it.fontSize!!).stringValue()?.parseValue()?.intValue()
                        contentText.setSpan(AbsoluteSizeSpan(fontSize!!, false), 0, contentText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    }

                    if (!it.color.isNullOrBlank()) {
                        val color = template.getValue(it.color!!).stringValue()
                        if (!color.isNullOrBlank()) {
                            contentText.setSpan(
                                ForegroundColorSpan(ColorParser.parseColor(color)),
                                0,
                                contentText.length,
                                Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                            )
                        }
                    }

                    if (it.textDecorationLine == "underline") {
                        contentText.setSpan(UnderlineSpan(), 0, contentText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    }
                    if (it.textDecorationLine == "line-through") {
                        contentText.setSpan(LineThroughSpan(), 0, contentText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    }

                    if (!it.fontWeight.isNullOrBlank()) {
                        val weight = template.getValue(it.fontWeight!!).stringValue()
                        if (weight == "bold" || weight == "semibold") {
                            contentText.setSpan(DSLParser.FakeBoldSpan(), 0, contentText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                        }
                    }

                    if (it.hasFontFamily()) {
                        val typeface = it.findTypeface(context)
                        if (typeface != null) {
                            tryCatch {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                                    contentText.setSpan(TypefaceSpan(typeface), 0, contentText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                } else {
                                    contentText.setSpan(CustomTypefaceSpan(typeface), 0, contentText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                                }
                            }
                        }
                    }

                    span.append(contentText)
                }
            } else if (!it.url.isNullOrBlank()) { // 图片
                val imageUrl = template.getValue(it.url).stringValue()

                if (!imageUrl.isNullOrBlank()) {
                    hasImage = true

                    var width = 0
                    if (!it.width.isNullOrBlank()) {
                        val widthStr = template.getValue(it.width).stringValue()
                        if (!widthStr.isNullOrBlank()) {
                            width = widthStr.parseValue().intValue()
                        }
                    }
                    var height = 0
                    if (!it.height.isNullOrBlank()) {
                        val heightStr = template.getValue(it.height).stringValue()
                        if (!heightStr.isNullOrBlank()) {
                            height = heightStr.parseValue().intValue()
                        }
                    }
                    val contentText = SpannableString("#image")
                    val imageSpan = WKMarginImageUrlSpan(imageUrl, this, width, height)
                    contentText.setSpan(imageSpan, 0, contentText.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
                    span.append(contentText)

                    imageSpans.add(imageSpan)
//                    imageSpan.load(span)
                }
            }
        }

        if (!hasImage) {
            getView().text = span
        } else {
            getView().setText(span, TextView.BufferType.SPANNABLE)
            imageSpans.forEach { it.load() }
        }


        renderMaxRows(baseViewModel.maxRows, template)
        getView().typeface = baseViewModel.findTypeface(context)
    }

    private fun renderSpan(baseViewModel: SpanViewModel, template: Template) {
        val text = baseViewModel.text ?: return
        val keywords = baseViewModel.keywords

        if (text.content.isNullOrBlank()) return
        val content = template.getValue(text.content!!).stringValue()

        val contentSpan = SpannableString(content ?: "")

        if (!content.isNullOrBlank()) {

            renderRichText(content, contentSpan, 0, text, template)

            for (keyword in keywords) {
                if (keyword.content.isNullOrBlank()) continue
                val keywordContent = template.getValue(keyword.content!!).stringValue()
                if (keywordContent.isNullOrBlank()) continue


                val startIndex = content.indexOf(keywordContent)
                // 校验索引
                if (startIndex < 0) continue // 没找到
                if (startIndex + keywordContent.length > content.length) continue // 范围不对

                renderRichText(keywordContent, contentSpan, startIndex, keyword, template)
            }
        }

        getView().text = contentSpan

        renderMaxRows(baseViewModel.maxRows, template)
        getView().typeface = baseViewModel.findTypeface(context)
    }

    private fun renderRichText(content: String, span: SpannableString, startIndex: Int, richText: RichTextModel, template: Template) {
        if (!richText.color.isNullOrBlank()) {
            val color = template.getValue(richText.color!!).stringValue()
            if (!color.isNullOrBlank()) {
                span.setSpan(
                    ForegroundColorSpan(ColorParser.parseColor(color)),
                    startIndex,
                    startIndex + content.length,
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                )
            }
        }
        if (!richText.fontWeight.isNullOrBlank()) {
            val weight = template.getValue(richText.fontWeight!!).stringValue()
            if (weight == "bold" || weight == "semibold") {
                span.setSpan(DSLParser.FakeBoldSpan(), startIndex, startIndex + content.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            }
        }
        if (richText.textDecorationLine == "underline") {
            span.setSpan(UnderlineSpan(), startIndex, startIndex + content.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }
        if (richText.textDecorationLine == "line-through") {
            span.setSpan(LineThroughSpan(), startIndex, startIndex + content.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        if (!richText.fontSize.isNullOrBlank()) {
            val fontSize = template.getValue(richText.fontSize!!).stringValue()?.parseValue()?.intValue()
            span.setSpan(AbsoluteSizeSpan(fontSize!!, false), startIndex, startIndex + content.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        if (richText.hasFontFamily()) {
            val typeface = richText.findTypeface(context)
            if (typeface != null) {
                tryCatch {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        span.setSpan(
                            TypefaceSpan(typeface),
                            startIndex,
                            startIndex + content.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    } else {
                        span.setSpan(
                            CustomTypefaceSpan(typeface),
                            startIndex,
                            startIndex + content.length,
                            Spannable.SPAN_INCLUSIVE_EXCLUSIVE
                        )
                    }
                }
            }
        }
    }

    private fun renderMaxRows(maxRows: String?, template: Template) {
        if (maxRows.isNullOrBlank()) return
        val max = template.getValue(maxRows).intValue() ?: 0
        if (max > 0) {
            getView().maxLines = max
            getView().ellipsize = TextUtils.TruncateAt.END
            getView().setLimitLines(max)
        } else {
            getView().maxLines = Int.MAX_VALUE
        }
    }

}