package com.hellobike.magiccube.parser.spans;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.text.style.LineHeightSpan;


import androidx.annotation.NonNull;

/**
 * 支持垂直居中的ImageSpan
 */
public class WKAlignMiddleImageSpan extends ImageSpan {

    public static final int ALIGN_MIDDLE = -100; // 不要和父类重复

    /**
     * 规定这个Span占几个字的宽度
     */
    private float mFontWidthMultiple = -1f;

    /**
     * 是否避免父类修改FontMetrics，如果为 false 则会走父类的逻辑, 会导致FontMetrics被更改
     */
    private boolean mAvoidSuperChangeFontMetrics = false;

    @SuppressWarnings("FieldCanBeLocal")
    private int mWidth;
    private Drawable mDrawable;
    private int mDrawableTintColorAttr;

    /**
     * @param d                 作为 span 的 Drawable
     * @param verticalAlignment 垂直对齐方式, 如果要垂直居中, 则使用 {@link #ALIGN_MIDDLE}
     */
    public WKAlignMiddleImageSpan(Drawable d, int verticalAlignment) {
        this(d, verticalAlignment, 0);
    }

    /**
     * @param d                 作为 span 的 Drawable
     * @param verticalAlignment 垂直对齐方式, 如果要垂直居中, 则使用 {@link #ALIGN_MIDDLE}
     * @param fontWidthMultiple 设置这个Span占几个中文字的宽度, 当该值 > 0 时, span 的宽度为该值*一个中文字的宽度; 当该值 <= 0 时, span 的宽度由 {@link #mAvoidSuperChangeFontMetrics} 决定
     */
    public WKAlignMiddleImageSpan(@NonNull Drawable d, int verticalAlignment, float fontWidthMultiple) {
        super(d.mutate(), verticalAlignment);
        mDrawable = getDrawable();
        if (fontWidthMultiple >= 0) {
            mFontWidthMultiple = fontWidthMultiple;
        }
    }

    @Override
    public int getSize(Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
        if (mAvoidSuperChangeFontMetrics) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            mWidth = rect.right;
        } else {
            mWidth = super.getSize(paint, text, start, end, fm);
        }
        if (mFontWidthMultiple > 0) {
            mWidth = (int) (paint.measureText("子") * mFontWidthMultiple);
        }
        return mWidth;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end,
                     float x, int top, int y, int bottom, Paint paint) {
        if (mVerticalAlignment == ALIGN_MIDDLE) {
            Drawable d = mDrawable;
            canvas.save();

//            // 注意如果这样实现会有问题：TextView 有 lineSpacing 时，这里 bottom 偏大，导致偏下
//            int transY = bottom - d.getBounds().bottom; // 底对齐
//            transY -= (paint.getFontMetricsInt().bottom - paint.getFontMetricsInt().top) / 2 - d.getBounds().bottom / 2; // 居中对齐
//            canvas.translate(x, transY);
//            d.draw(canvas);
//            canvas.restore();

            // 文字四条线（基准baseline，baseline 上为负，下为正）：top ascent baseline descent bottom

            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int fontTop = y + fontMetricsInt.top;
            int fontMetricsHeight = fontMetricsInt.bottom - fontMetricsInt.top; // 文字的高度： bottom - top
            int iconHeight = d.getBounds().bottom - d.getBounds().top; // 图片的高度
            int iconTop = fontTop + (fontMetricsHeight - iconHeight) / 2;
            canvas.translate(x, iconTop); // 移动到中心对齐的位置
            d.draw(canvas);
            canvas.restore();
        } else {
            super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        }
    }

    /**
     * 是否避免父类修改FontMetrics，如果为 false 则会走父类的逻辑, 会导致FontMetrics被更改
     */
    public void setAvoidSuperChangeFontMetrics(boolean avoidSuperChangeFontMetrics) {
        mAvoidSuperChangeFontMetrics = avoidSuperChangeFontMetrics;
    }

    private boolean isInitOriginArgs = false;
    private int originAscent = -1;
    private int originDescent = -1;
    private int originTop = -1;
    private int originBottom = -1;

//    @Override
//    public void chooseHeight(CharSequence text, int start, int end, int spanstartv, int lineHeight, Paint.FontMetricsInt fm) {
//
//

//        if (text instanceof Spanned) {
//            Spanned textSpan = (Spanned) text;
//            int spanStart = textSpan.getSpanStart(this);
//            int spanEnd = textSpan.getSpanEnd(this);
//
//            if (!isInitOriginArgs) {
//                originTop = fm.top;
//                originAscent = fm.ascent;
//                originDescent = fm.descent;
//                originBottom = fm.bottom;
//                isInitOriginArgs = true;
//            }
//
//            if (spanStart >= start && spanEnd >= start && spanEnd <= end && spanEnd > spanStart) {
//
//                int intrinsicHeight = mDrawable.getIntrinsicHeight();
//
//                int need = intrinsicHeight - (lineHeight + fm.descent - fm.ascent - spanstartv);
//
//                if (need > 0) {
//                    fm.descent += need;
//                }
//
//                need = intrinsicHeight - (lineHeight + fm.bottom - fm.top - spanstartv);
//
//                if (need > 0) {
//                    fm.bottom += need;
//                }
//            } else {
//                // 恢复行距
////            fm.ascent = originAscent
////            fm.top = originTop
////            fm.descent = originDescent
////            fm.bottom = originBottom
//            }
//
//
//        }
//    }
}
