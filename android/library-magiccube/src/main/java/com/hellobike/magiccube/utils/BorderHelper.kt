package com.hellobike.magiccube.utils

import android.annotation.TargetApi
import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.hellobike.library_magiccube.R
import com.hellobike.magiccube.widget.IMTLayout
import com.hellobike.magiccube.widget.IMTLayout.HideRadiusSide
import java.lang.ref.WeakReference


/**
 *
 * @Description:     View圆角和边框属性
 * @Author:         nikozxh
 * @CreateDate:     2021/2/23 11:24 AM
 */
@Suppress("DEPRECATION")
class BorderHelper:IMTLayout {

    val RADIUS_OF_HALF_VIEW_HEIGHT = -1
    val RADIUS_OF_HALF_VIEW_WIDTH = -2
    private var mContext: Context? = null

    // size
    private var mWidthLimit = 0
    private var mHeightLimit = 0
    private var mWidthMini = 0
    private var mHeightMini = 0


    // divider
    private var mTopDividerHeight = 0
    private var mTopDividerInsetLeft = 0
    private var mTopDividerInsetRight = 0
    private var mTopDividerColor = 0
    private var mTopDividerAlpha = 255

    private var mBottomDividerHeight = 0
    private var mBottomDividerInsetLeft = 0
    private var mBottomDividerInsetRight = 0
    private var mBottomDividerColor = 0
    private var mBottomDividerAlpha = 255

    private var mLeftDividerWidth = 0
    private var mLeftDividerInsetTop = 0
    private var mLeftDividerInsetBottom = 0
    private var mLeftDividerColor = 0
    private var mLeftDividerAlpha = 255

    private var mRightDividerWidth = 0
    private var mRightDividerInsetTop = 0
    private var mRightDividerInsetBottom = 0
    private var mRightDividerColor = 0
    private var mRightDividerAlpha = 255
    private var mDividerPaint: Paint? = null

    // round
    private var mClipPaint: Paint? = null
    private var mMode: PorterDuffXfermode? = null
    private var mRadius = 0

    @HideRadiusSide
    private var mHideRadiusSide = IMTLayout.HIDE_RADIUS_SIDE_NONE
    private var mRadiusArray: FloatArray? = null
    private var mShouldUseRadiusArray = false
    private var mBorderRect: RectF? = null
    private var mBorderColor = 0
    private var mBorderWidth = 0
    private var mOuterNormalColor = 0
    private var mOwner: WeakReference<View>? = null
    private var mIsOutlineExcludePadding = false
    private val mPath = Path()

    // shadow
    private var mIsShowBorderOnlyBeforeL = true
    private var mShadowElevation = 0
    private var mShadowAlpha = 0f
    private var mShadowColor = Color.BLACK

    // outline inset
    private var mOutlineInsetLeft = 0
    private var mOutlineInsetRight = 0
    private var mOutlineInsetTop = 0
    private var mOutlineInsetBottom = 0

    constructor(context: Context?, owner: View) {
        mContext = context
        mOwner = WeakReference(owner)
        mBorderRect = RectF()
        mMode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        mClipPaint = Paint()
        mClipPaint!!.isAntiAlias = true
        mBorderRect = RectF()
    }
    

    override fun setUseThemeGeneralShadowElevation() {
        setRadiusAndShadow(mRadius, mHideRadiusSide, mShadowElevation, mShadowAlpha)
    }

    override fun setOutlineExcludePadding(outlineExcludePadding: Boolean) {
        if (useFeature()) {
            val owner: View = mOwner?.get() ?: return
            mIsOutlineExcludePadding = outlineExcludePadding
            owner.invalidateOutline()
        }
    }

    override fun setWidthLimit(widthLimit: Int): Boolean {
        if (mWidthLimit != widthLimit) {
            mWidthLimit = widthLimit
            return true
        }
        return false
    }

    override fun setHeightLimit(heightLimit: Int): Boolean {
        if (mHeightLimit != heightLimit) {
            mHeightLimit = heightLimit
            return true
        }
        return false
    }

    override fun updateLeftSeparatorColor(color: Int) {
        if (mLeftDividerColor != color) {
            mLeftDividerColor = color
            invalidate()
        }
    }

    override fun updateBottomSeparatorColor(color: Int) {
        if (mBottomDividerColor != color) {
            mBottomDividerColor = color
            invalidate()
        }
    }

    override fun updateTopSeparatorColor(color: Int) {
        if (mTopDividerColor != color) {
            mTopDividerColor = color
            invalidate()
        }
    }

    override fun updateRightSeparatorColor(color: Int) {
        if (mRightDividerColor != color) {
            mRightDividerColor = color
            invalidate()
        }
    }

    override fun getShadowElevation(): Int {
        return mShadowElevation
    }

    override fun getShadowAlpha(): Float {
        return mShadowAlpha
    }

    override fun getShadowColor(): Int {
        return mShadowColor
    }

    override fun setOutlineInset(left: Int, top: Int, right: Int, bottom: Int) {
        if (useFeature()) {
            val owner: View = mOwner?.get() ?: return
            mOutlineInsetLeft = left
            mOutlineInsetRight = right
            mOutlineInsetTop = top
            mOutlineInsetBottom = bottom
            owner.invalidateOutline()
        }
    }


    override fun setShowBorderOnlyBeforeL(showBorderOnlyBeforeL: Boolean) {
        mIsShowBorderOnlyBeforeL = showBorderOnlyBeforeL
        invalidate()
    }

    override fun setShadowElevation(elevation: Int) {
        if (mShadowElevation == elevation) {
            return
        }
        mShadowElevation = elevation
        invalidateOutline()
    }

    override fun setShadowAlpha(shadowAlpha: Float) {
        if (mShadowAlpha == shadowAlpha) {
            return
        }
        mShadowAlpha = shadowAlpha
        invalidateOutline()
    }

    override fun setShadowColor(shadowColor: Int) {
        if (mShadowColor == shadowColor) {
            return
        }
        mShadowColor = shadowColor
        setShadowColorInner(mShadowColor)
    }

    private fun setShadowColorInner(shadowColor: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val owner: View = mOwner?.get() ?: return
            owner.outlineAmbientShadowColor = shadowColor
            owner.outlineSpotShadowColor = shadowColor
        }
    }

    private fun invalidateOutline() {
        if (useFeature()) {
            val owner: View = mOwner?.get() ?: return
            if (mShadowElevation == 0) {
                owner.elevation = 0f
            } else {
                owner.elevation = mShadowElevation.toFloat()
            }
            owner.invalidateOutline()
        }
    }

    private fun invalidate() {
        val owner: View = mOwner?.get() ?: return
        owner.invalidate()
    }

    override fun setHideRadiusSide(@HideRadiusSide hideRadiusSide: Int) {
        if (mHideRadiusSide == hideRadiusSide) {
            return
        }
        setRadiusAndShadow(mRadius, hideRadiusSide, mShadowElevation, mShadowAlpha)
    }

    override fun getHideRadiusSide(): Int {
        return mHideRadiusSide
    }

    override fun setRadius(radius: Int) {
        if (mRadius != radius) {
            setRadiusAndShadow(radius, mShadowElevation, mShadowAlpha)
        }
    }

    private var mRadii: FloatArray? = null
    private val mRadiiRectF: RectF = RectF()
    private val mRadiiPath: Path = Path()
//    private val mRadiiBorderRectF: RectF = RectF()
//    private val mRadiiBorderPath: Path = Path()

    fun setRadii(lt: Float, rt: Float, rb: Float, lb: Float) {
        val radii = floatArrayOf(lt, lt, rt, rt, rb, rb, lb, lb)
        if (!radii.contentEquals(mRadii)) {
            this.mRadii = radii
            setRadiusAndShadow(0, mShadowElevation, mShadowAlpha)
        }
    }

    override fun setRadius(radius: Int, @HideRadiusSide hideRadiusSide: Int) {
        if (mRadius == radius && hideRadiusSide == mHideRadiusSide) {
            return
        }
        setRadiusAndShadow(radius, hideRadiusSide, mShadowElevation, mShadowAlpha)
    }

    override fun getRadius(): Int {
        return mRadius
    }

    override fun setRadiusAndShadow(radius: Int, shadowElevation: Int, shadowAlpha: Float) {
        setRadiusAndShadow(radius, mHideRadiusSide, shadowElevation, shadowAlpha)
    }

    override fun setRadiusAndShadow(radius: Int, @HideRadiusSide hideRadiusSide: Int, shadowElevation: Int, shadowAlpha: Float) {
        setRadiusAndShadow(radius, hideRadiusSide, shadowElevation, mShadowColor, shadowAlpha)
    }

    override fun setRadiusAndShadow(radius: Int, hideRadiusSide: Int, shadowElevation: Int, shadowColor: Int, shadowAlpha: Float) {
        val owner: View = mOwner?.get() ?: return
        mRadius = radius
        mHideRadiusSide = hideRadiusSide
        mShouldUseRadiusArray = isRadiusWithSideHidden()
        mShadowElevation = shadowElevation
        mShadowAlpha = shadowAlpha
        mShadowColor = shadowColor
        if (useFeature() && mRadii?.size != 8) {
            if (mShadowElevation == 0 || mShouldUseRadiusArray) {
                owner.elevation = 0f
            } else {
                owner.elevation = mShadowElevation.toFloat()
            }
            setShadowColorInner(mShadowColor)
            owner.outlineProvider = object : ViewOutlineProvider() {
                @TargetApi(21)
                override fun getOutline(view: View, outline: Outline) {
                    val w = view.width
                    val h = view.height
                    if (w == 0 || h == 0) {
                        return
                    }
                    var radius = getRealRadius().toFloat()
                    val min = Math.min(w, h)
                    if (radius * 2 > min) {
                        // 解决 OnePlus 3T 8.0 上显示变形
                        radius = min / 2f
                    }
                    if (mShouldUseRadiusArray) {
                        var left = 0
                        var top = 0
                        var right = w
                        var bottom = h
                        if (mHideRadiusSide == IMTLayout.HIDE_RADIUS_SIDE_LEFT) {
                            left -= radius.toInt()
                        } else if (mHideRadiusSide == IMTLayout.HIDE_RADIUS_SIDE_TOP) {
                            top -= radius.toInt()
                        } else if (mHideRadiusSide == IMTLayout.HIDE_RADIUS_SIDE_RIGHT) {
                            right += radius.toInt()
                        } else if (mHideRadiusSide == IMTLayout.HIDE_RADIUS_SIDE_BOTTOM) {
                            bottom += radius.toInt()
                        }
                        outline.setRoundRect(left, top,
                                right, bottom, radius)
                        return
                    }
                    var top = mOutlineInsetTop
                    var bottom = Math.max(top + 1, h - mOutlineInsetBottom)
                    var left = mOutlineInsetLeft
                    var right = w - mOutlineInsetRight
                    if (mIsOutlineExcludePadding) {
                        left += view.paddingLeft
                        top += view.paddingTop
                        right = Math.max(left + 1, right - view.paddingRight)
                        bottom = Math.max(top + 1, bottom - view.paddingBottom)
                    }
                    var shadowAlpha = mShadowAlpha
                    if (mShadowElevation == 0) {
                        // outline.setAlpha will work even if shadowElevation == 0
                        shadowAlpha = 1f
                    }
                    outline.alpha = shadowAlpha
                    if (radius <= 0) {
                        outline.setRect(left, top,
                                right, bottom)
                    } else {
                        outline.setRoundRect(left, top,
                                right, bottom, radius)
                    }
                }
            }
            owner.clipToOutline = mRadius == RADIUS_OF_HALF_VIEW_WIDTH || mRadius == RADIUS_OF_HALF_VIEW_HEIGHT || mRadius > 0
        }
        owner.invalidate()
    }

    /**
     * 有radius, 但是有一边不显示radius。
     *
     * @return
     */
    fun isRadiusWithSideHidden(): Boolean {
        return (mRadius == RADIUS_OF_HALF_VIEW_HEIGHT || mRadius == RADIUS_OF_HALF_VIEW_WIDTH || mRadius > 0) && mHideRadiusSide != IMTLayout.HIDE_RADIUS_SIDE_NONE
    }

    override fun updateTopDivider(topInsetLeft: Int, topInsetRight: Int, topDividerHeight: Int, topDividerColor: Int) {
        mTopDividerInsetLeft = topInsetLeft
        mTopDividerInsetRight = topInsetRight
        mTopDividerHeight = topDividerHeight
        mTopDividerColor = topDividerColor
    }

    override fun updateBottomDivider(bottomInsetLeft: Int, bottomInsetRight: Int, bottomDividerHeight: Int, bottomDividerColor: Int) {
        mBottomDividerInsetLeft = bottomInsetLeft
        mBottomDividerInsetRight = bottomInsetRight
        mBottomDividerColor = bottomDividerColor
        mBottomDividerHeight = bottomDividerHeight
    }

    override fun updateLeftDivider(leftInsetTop: Int, leftInsetBottom: Int, leftDividerWidth: Int, leftDividerColor: Int) {
        mLeftDividerInsetTop = leftInsetTop
        mLeftDividerInsetBottom = leftInsetBottom
        mLeftDividerWidth = leftDividerWidth
        mLeftDividerColor = leftDividerColor
    }

    override fun updateRightDivider(rightInsetTop: Int, rightInsetBottom: Int, rightDividerWidth: Int, rightDividerColor: Int) {
        mRightDividerInsetTop = rightInsetTop
        mRightDividerInsetBottom = rightInsetBottom
        mRightDividerWidth = rightDividerWidth
        mRightDividerColor = rightDividerColor
    }

    override fun onlyShowTopDivider(topInsetLeft: Int, topInsetRight: Int,
                                    topDividerHeight: Int, topDividerColor: Int) {
        updateTopDivider(topInsetLeft, topInsetRight, topDividerHeight, topDividerColor)
        mLeftDividerWidth = 0
        mRightDividerWidth = 0
        mBottomDividerHeight = 0
    }

    override fun onlyShowBottomDivider(bottomInsetLeft: Int, bottomInsetRight: Int,
                                       bottomDividerHeight: Int, bottomDividerColor: Int) {
        updateBottomDivider(bottomInsetLeft, bottomInsetRight, bottomDividerHeight, bottomDividerColor)
        mLeftDividerWidth = 0
        mRightDividerWidth = 0
        mTopDividerHeight = 0
    }

    override fun onlyShowLeftDivider(leftInsetTop: Int, leftInsetBottom: Int, leftDividerWidth: Int, leftDividerColor: Int) {
        updateLeftDivider(leftInsetTop, leftInsetBottom, leftDividerWidth, leftDividerColor)
        mRightDividerWidth = 0
        mTopDividerHeight = 0
        mBottomDividerHeight = 0
    }

    override fun onlyShowRightDivider(rightInsetTop: Int, rightInsetBottom: Int, rightDividerWidth: Int, rightDividerColor: Int) {
        updateRightDivider(rightInsetTop, rightInsetBottom, rightDividerWidth, rightDividerColor)
        mLeftDividerWidth = 0
        mTopDividerHeight = 0
        mBottomDividerHeight = 0
    }

    override fun setTopDividerAlpha(dividerAlpha: Int) {
        mTopDividerAlpha = dividerAlpha
    }

    override fun setBottomDividerAlpha(dividerAlpha: Int) {
        mBottomDividerAlpha = dividerAlpha
    }

    override fun setLeftDividerAlpha(dividerAlpha: Int) {
        mLeftDividerAlpha = dividerAlpha
    }

    override fun setRightDividerAlpha(dividerAlpha: Int) {
        mRightDividerAlpha = dividerAlpha
    }


    fun handleMiniWidth(widthMeasureSpec: Int, measuredWidth: Int): Int {
        return if (View.MeasureSpec.getMode(widthMeasureSpec) != View.MeasureSpec.EXACTLY
                && measuredWidth < mWidthMini) {
            View.MeasureSpec.makeMeasureSpec(mWidthMini, View.MeasureSpec.EXACTLY)
        } else widthMeasureSpec
    }

    fun handleMiniHeight(heightMeasureSpec: Int, measuredHeight: Int): Int {
        return if (View.MeasureSpec.getMode(heightMeasureSpec) != View.MeasureSpec.EXACTLY
                && measuredHeight < mHeightMini) {
            View.MeasureSpec.makeMeasureSpec(mHeightMini, View.MeasureSpec.EXACTLY)
        } else heightMeasureSpec
    }

    fun getMeasuredWidthSpec(widthMeasureSpec: Int): Int {
        var widthMeasureSpec = widthMeasureSpec
        if (mWidthLimit > 0) {
            val size = View.MeasureSpec.getSize(widthMeasureSpec)
            if (size > mWidthLimit) {
                val mode = View.MeasureSpec.getMode(widthMeasureSpec)
                widthMeasureSpec = if (mode == View.MeasureSpec.AT_MOST) {
                    View.MeasureSpec.makeMeasureSpec(mWidthLimit, View.MeasureSpec.AT_MOST)
                } else {
                    View.MeasureSpec.makeMeasureSpec(mWidthLimit, View.MeasureSpec.EXACTLY)
                }
            }
        }
        return widthMeasureSpec
    }

    fun getMeasuredHeightSpec(heightMeasureSpec: Int): Int {
        var heightMeasureSpec = heightMeasureSpec
        if (mHeightLimit > 0) {
            val size = View.MeasureSpec.getSize(heightMeasureSpec)
            if (size > mHeightLimit) {
                val mode = View.MeasureSpec.getMode(heightMeasureSpec)
                heightMeasureSpec = if (mode == View.MeasureSpec.AT_MOST) {
                    View.MeasureSpec.makeMeasureSpec(mWidthLimit, View.MeasureSpec.AT_MOST)
                } else {
                    View.MeasureSpec.makeMeasureSpec(mWidthLimit, View.MeasureSpec.EXACTLY)
                }
            }
        }
        return heightMeasureSpec
    }

    override fun setBorderColor(@ColorInt borderColor: Int) {
        mBorderColor = borderColor

    }

    override fun setBorderWidth(borderWidth: Int) {
        mBorderWidth = borderWidth
    }

    override fun setOuterNormalColor(color: Int) {
        mOuterNormalColor = color
    }

    override fun hasTopSeparator(): Boolean {
        return mTopDividerHeight > 0
    }

    override fun hasRightSeparator(): Boolean {
        return mRightDividerWidth > 0
    }

    override fun hasBottomSeparator(): Boolean {
        return mBottomDividerHeight > 0
    }

    override fun hasLeftSeparator(): Boolean {
        return mLeftDividerWidth > 0
    }

    override fun hasBorder(): Boolean {
        return mBorderWidth > 0
    }

    fun drawDividers(canvas: Canvas, w: Int, h: Int) {
        val owner: View = mOwner?.get() ?: return
        if (mDividerPaint == null &&
                (mTopDividerHeight > 0 || mBottomDividerHeight > 0 || mLeftDividerWidth > 0 || mRightDividerWidth > 0)) {
            mDividerPaint = Paint()
        }
        canvas.save()
        canvas.translate(owner.scrollX.toFloat(), owner.scrollY.toFloat())
        if (mTopDividerHeight > 0) {
            mDividerPaint!!.strokeWidth = mTopDividerHeight.toFloat()
            mDividerPaint!!.color = mTopDividerColor
            if (mTopDividerAlpha < 255) {
                mDividerPaint!!.alpha = mTopDividerAlpha
            }
            val y = mTopDividerHeight / 2f
            canvas.drawLine(mTopDividerInsetLeft.toFloat(), y, (w - mTopDividerInsetRight).toFloat(), y, mDividerPaint!!)
        }
        if (mBottomDividerHeight > 0) {
            mDividerPaint!!.strokeWidth = mBottomDividerHeight.toFloat()
            mDividerPaint!!.color = mBottomDividerColor
            if (mBottomDividerAlpha < 255) {
                mDividerPaint!!.alpha = mBottomDividerAlpha
            }
            val y = Math.floor(h - mBottomDividerHeight / 2f.toDouble()).toFloat()
            canvas.drawLine(mBottomDividerInsetLeft.toFloat(), y, (w - mBottomDividerInsetRight).toFloat(), y, mDividerPaint!!)
        }
        if (mLeftDividerWidth > 0) {
            mDividerPaint!!.strokeWidth = mLeftDividerWidth.toFloat()
            mDividerPaint!!.color = mLeftDividerColor
            if (mLeftDividerAlpha < 255) {
                mDividerPaint!!.alpha = mLeftDividerAlpha
            }
            val x = mLeftDividerWidth / 2f
            canvas.drawLine(x, mLeftDividerInsetTop.toFloat(), x, (h - mLeftDividerInsetBottom).toFloat(), mDividerPaint!!)
        }
        if (mRightDividerWidth > 0) {
            mDividerPaint!!.strokeWidth = mRightDividerWidth.toFloat()
            mDividerPaint!!.color = mRightDividerColor
            if (mRightDividerAlpha < 255) {
                mDividerPaint!!.alpha = mRightDividerAlpha
            }
            val x = Math.floor(w - mRightDividerWidth / 2f.toDouble()).toFloat()
            canvas.drawLine(x, mRightDividerInsetTop.toFloat(), x, (h - mRightDividerInsetBottom).toFloat(), mDividerPaint!!)
        }
        canvas.restore()
    }


    private fun getRealRadius(): Int {
        val owner: View = mOwner?.get() ?: return mRadius
        val radius: Int
        radius = if (mRadius == RADIUS_OF_HALF_VIEW_HEIGHT) {
            owner.height / 2
        } else if (mRadius == RADIUS_OF_HALF_VIEW_WIDTH) {
            owner.width / 2
        } else {
            mRadius
        }
        return radius
    }

    fun draw(canvas: Canvas) {
        val radii = mRadii
        val view = mOwner?.get() ?: return
        if (radii?.size == 8) { // 是局部圆角

            val h = view.height
            val w = view.width

            if (w == 0 || h == 0) {
                return
            }

            var top = mOutlineInsetTop
            var bottom = Math.max(top + 1, h - mOutlineInsetBottom)
            var left = mOutlineInsetLeft
            var right = w - mOutlineInsetRight
            if (mIsOutlineExcludePadding) {
                left += view.paddingLeft
                top += view.paddingTop
                right = Math.max(left + 1, right - view.paddingRight)
                bottom = Math.max(top + 1, bottom - view.paddingBottom)
            }

            mRadiiRectF.set(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
            mRadiiPath.reset()
            mRadiiPath.addRoundRect(mRadiiRectF, mRadii!!, Path.Direction.CCW)


//            mRadiiBorderPath.reset()
//            mRadiiBorderRectF.set(0f, 0f, 0f, 0f)
//            val halfBorderWidth = mBorderWidth * 1.0f / 2
//            if (halfBorderWidth > 0 && mBorderColor != 0) {
//                mRadiiBorderRectF.set(left + halfBorderWidth, top + halfBorderWidth, right - halfBorderWidth, bottom - halfBorderWidth)
//                mRadiiBorderPath.addRoundRect(mRadiiBorderRectF, mRadii!!, Path.Direction.CCW)
//            }



            canvas.clipPath(mRadiiPath)
//            mRadiiPath.addRoundRect(mRadiiRectF, 40.0f, 40.0f, Path.Direction.CW)
//
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
//                mRadiiTempPath.reset()
//                mRadiiTempPath.addRect(mRadiiRectF, Path.Direction.CCW)
//                // DIFFERENCE -- 减去Path2后Path1区域剩下的部分
//                // INTERSECT --- 保留Path2 和 Path1 共同的部分
//                // UNION -- 保留Path1 和 Path 2
//                // XOR --- 保留Path1 和 Path2 去除共同的部分
//                // REVERSE_DIFFERENCE --- 减去Path1后Path2区域剩下的部分
//                mRadiiTempPath.op(
//                    mRadiiPath,
//                    Path.Op.DIFFERENCE
//                ) // 减去 mRadiiPath 后 mRadiiTempPath 区域剩下的部分
//            }
//            canvas.save()
        }
    }

    fun dispatchRoundBorderDraw(canvas: Canvas) {
        val needDrawBorder = mBorderWidth > 0 && mBorderColor != 0

        val owner: View = mOwner?.get() ?: return
        val radius = getRealRadius()
        val needCheckFakeOuterNormalDraw = radius > 0 && !useFeature() && mOuterNormalColor != 0
        if (!needCheckFakeOuterNormalDraw && !needDrawBorder) {
            return
        }

        if (mIsShowBorderOnlyBeforeL && useFeature() && mShadowElevation != 0) {
            return
        }
        val width = canvas.width
        val height = canvas.height
        canvas.save()
        canvas.translate(owner.scrollX.toFloat(), owner.scrollY.toFloat())

        val radii = mRadii
        if (radii?.size == 8) {
            if (needDrawBorder) { // draw path 的方式绘制边框，会内外各绘制一半所以需要 *2
                mClipPaint!!.color = mBorderColor
                mClipPaint!!.strokeWidth = mBorderWidth.toFloat() * 2
                mClipPaint!!.style = Paint.Style.STROKE
                canvas.drawPath(mRadiiPath, mClipPaint!!)
//                canvas.drawPath(mRadiiBorderPath, mClipPaint!!)
            }
            canvas.restore()
        } else {
            // react
            val halfBorderWith = mBorderWidth / 2f
            if (mIsOutlineExcludePadding) {
                mBorderRect!![owner.paddingLeft + halfBorderWith, owner.paddingTop + halfBorderWith, width - owner.paddingRight - halfBorderWith] = height - owner.paddingBottom - halfBorderWith
            } else {
                mBorderRect!![halfBorderWith, halfBorderWith, width - halfBorderWith] = height - halfBorderWith
            }
            if (mShouldUseRadiusArray) {
                if (mRadiusArray == null) {
                    mRadiusArray = FloatArray(8)
                }
                if (mHideRadiusSide == IMTLayout.HIDE_RADIUS_SIDE_TOP) {
                    mRadiusArray!![4] = radius.toFloat()
                    mRadiusArray!![5] = radius.toFloat()
                    mRadiusArray!![6] = radius.toFloat()
                    mRadiusArray!![7] = radius.toFloat()
                } else if (mHideRadiusSide == IMTLayout.HIDE_RADIUS_SIDE_RIGHT) {
                    mRadiusArray!![0] = radius.toFloat()
                    mRadiusArray!![1] = radius.toFloat()
                    mRadiusArray!![6] = radius.toFloat()
                    mRadiusArray!![7] = radius.toFloat()
                } else if (mHideRadiusSide == IMTLayout.HIDE_RADIUS_SIDE_BOTTOM) {
                    mRadiusArray!![0] = radius.toFloat()
                    mRadiusArray!![1] = radius.toFloat()
                    mRadiusArray!![2] = radius.toFloat()
                    mRadiusArray!![3] = radius.toFloat()
                } else if (mHideRadiusSide == IMTLayout.HIDE_RADIUS_SIDE_LEFT) {
                    mRadiusArray!![2] = radius.toFloat()
                    mRadiusArray!![3] = radius.toFloat()
                    mRadiusArray!![4] = radius.toFloat()
                    mRadiusArray!![5] = radius.toFloat()
                }
            }
            if (needCheckFakeOuterNormalDraw) {
                val layerId = canvas.saveLayer(0F, 0F, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)
                canvas.drawColor(mOuterNormalColor)
                mClipPaint!!.color = mOuterNormalColor
                mClipPaint!!.style = Paint.Style.FILL
                mClipPaint!!.xfermode = mMode
                if (!mShouldUseRadiusArray) {
                    canvas.drawRoundRect(mBorderRect!!, radius.toFloat(), radius.toFloat(), mClipPaint!!)
                } else {
                    drawRoundRect(canvas, mBorderRect, mRadiusArray, mClipPaint)
                }
                mClipPaint!!.xfermode = null
                canvas.restoreToCount(layerId)
            }
            if (needDrawBorder&&mBorderRect!=null) {
                mClipPaint!!.color = mBorderColor
                mClipPaint!!.strokeWidth = mBorderWidth.toFloat()
                mClipPaint!!.style = Paint.Style.STROKE
                if (mShouldUseRadiusArray) {
                    drawRoundRect(canvas, mBorderRect, mRadiusArray, mClipPaint)
                } else if (radius <= 0) {
                    canvas.drawRect(mBorderRect!!, mClipPaint!!)
                } else {
                    canvas.drawRoundRect(mBorderRect!!, radius.toFloat(), radius.toFloat(), mClipPaint!!)
                }
            }
            canvas.restore()
        }
    }

    private fun drawRoundRect(canvas: Canvas, rect: RectF?, radiusArray: FloatArray?, paint: Paint?) {
        mPath.reset()
        mPath.addRoundRect(rect!!, radiusArray!!, Path.Direction.CW)
        canvas.drawPath(mPath, paint!!)
    }

    private fun useFeature(): Boolean {
        return Build.VERSION.SDK_INT >= 21
    }

}

