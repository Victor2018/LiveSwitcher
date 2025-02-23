package com.quick.liveswitcher.ui.dialog

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.*
import android.widget.PopupWindow
import androidx.annotation.IntDef
import androidx.core.widget.PopupWindowCompat
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

open abstract class RelativePopupWindow: PopupWindow {

    protected abstract fun bindContentView(): Int
    protected abstract fun getPopWidth(): Int
    protected abstract fun getPopHeight(): Int
    protected abstract fun initView(view: View?)

    @IntDef(
        VerticalPosition.CENTER,
        VerticalPosition.ABOVE,
        VerticalPosition.BELOW,
        VerticalPosition.ALIGN_TOP,
        VerticalPosition.ALIGN_BOTTOM
    )
    @Retention(
        RetentionPolicy.SOURCE
    )
    annotation class VerticalPosition {
        companion object {
            const val CENTER = 0
            const val ABOVE = 1
            const val BELOW = 2
            const val ALIGN_TOP = 3
            const val ALIGN_BOTTOM = 4
        }
    }

    @IntDef(
        HorizontalPosition.CENTER,
        HorizontalPosition.LEFT,
        HorizontalPosition.RIGHT,
        HorizontalPosition.ALIGN_LEFT,
        HorizontalPosition.ALIGN_RIGHT
    )
    @Retention(
        RetentionPolicy.SOURCE
    )
    annotation class HorizontalPosition {
        companion object {
            const val CENTER = 0
            const val LEFT = 1
            const val RIGHT = 2
            const val ALIGN_LEFT = 3
            const val ALIGN_RIGHT = 4
        }
    }

    var mContext: Context? = null
    private var mWidth = 0
    private var mHeight = 0

    constructor(context: Context?) : this(context,null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        mContext = context
        handleWindow()
        initialize()
    }

    fun initialize() {
        contentView = LayoutInflater.from(mContext).inflate(bindContentView(), null)
        initView(contentView)
    }

    fun handleWindow() {
        //设置popwindow弹出窗体可点击
        isFocusable = true
        isOutsideTouchable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mWidth = getPopWidth()
        mHeight = getPopHeight()

        //设置popwindow弹出窗体的宽
        width = (if (mWidth != 0) mWidth else ViewGroup.LayoutParams.WRAP_CONTENT)
        //设置popwindow弹出窗体的高
        height = (if (mHeight != 0) mHeight else ViewGroup.LayoutParams.WRAP_CONTENT)

    }

    /**
     * Show at relative position to anchor View.
     * @param anchor Anchor View
     * @param vertPos Vertical Position Flag
     * @param horizPos Horizontal Position Flag
     */
    fun showOnAnchor(
        anchor: View,
        @VerticalPosition vertPos: Int,
        @HorizontalPosition horizPos: Int
    ) {
        showOnAnchor(anchor, vertPos, horizPos, 0, 0)
    }

    /**
     * Show at relative position to anchor View.
     * @param anchor Anchor View
     * @param vertPos Vertical Position Flag
     * @param horizPos Horizontal Position Flag
     * @param fitInScreen Automatically fit in screen or not
     */
    fun showOnAnchor(
        anchor: View,
        @VerticalPosition vertPos: Int,
        @HorizontalPosition horizPos: Int,
        fitInScreen: Boolean
    ) {
        showOnAnchor(anchor, vertPos, horizPos, 0, 0, fitInScreen)
    }

    /**
     * Show at relative position to anchor View with translation.
     * @param anchor Anchor View
     * @param vertPos Vertical Position Flag
     * @param horizPos Horizontal Position Flag
     * @param x Translation X
     * @param y Translation Y
     */
    fun showOnAnchor(
        anchor: View,
        @VerticalPosition vertPos: Int,
        @HorizontalPosition horizPos: Int,
        x: Int,
        y: Int
    ) {
        showOnAnchor(anchor, vertPos, horizPos, x, y, true)
    }

    /**
     * Show at relative position to anchor View with translation.
     * @param anchor Anchor View
     * @param vertPos Vertical Position Flag
     * @param horizPos Horizontal Position Flag
     * @param x Translation X
     * @param y Translation Y
     * @param fitInScreen Automatically fit in screen or not
     */
    fun showOnAnchor(
        anchor: View,
        @VerticalPosition vertPos: Int,
        @HorizontalPosition horizPos: Int,
        x: Int,
        y: Int,
        fitInScreen: Boolean
    ) {
        var x = x
        var y = y
        isClippingEnabled = fitInScreen
        val contentView = contentView
        val windowRect = Rect()
        contentView.getWindowVisibleDisplayFrame(windowRect)
        val windowW = windowRect.width()
        val windowH = windowRect.height()
        contentView.measure(
            makeDropDownMeasureSpec(width, windowW),
            makeDropDownMeasureSpec(height, windowH)
        )
        val measuredW = contentView.measuredWidth
        val measuredH = contentView.measuredHeight
        val anchorLocation = IntArray(2)
        anchor.getLocationInWindow(anchorLocation)
        val anchorBottom = anchorLocation[1] + anchor.height
        if (!fitInScreen) {
            x += anchorLocation[0]
            y += anchorBottom
        }
        when (vertPos) {
            VerticalPosition.ABOVE -> y -= measuredH + anchor.height
            VerticalPosition.ALIGN_BOTTOM -> y -= measuredH
            VerticalPosition.CENTER -> y -= anchor.height / 2 + measuredH / 2
            VerticalPosition.ALIGN_TOP -> y -= anchor.height
            VerticalPosition.BELOW -> {}
        }
        when (horizPos) {
            HorizontalPosition.LEFT -> x -= measuredW
            HorizontalPosition.ALIGN_RIGHT -> x -= measuredW - anchor.width
            HorizontalPosition.CENTER -> x += anchor.width / 2 - measuredW / 2
            HorizontalPosition.ALIGN_LEFT -> {}
            HorizontalPosition.RIGHT -> x += anchor.width
        }
        if (fitInScreen) {
            if (y + anchorBottom < 0) {
                y = -anchorBottom
            } else if (y + anchorBottom + measuredH > windowH) {
                y = windowH - anchorBottom - measuredH
            }
            PopupWindowCompat.showAsDropDown(this, anchor, x, y, Gravity.NO_GRAVITY)
        } else {
            showAtLocation(anchor, Gravity.NO_GRAVITY, x, y)
        }
    }

    private fun makeDropDownMeasureSpec(measureSpec: Int, maxSize: Int): Int {
        return View.MeasureSpec.makeMeasureSpec(
            getDropDownMeasureSpecSize(measureSpec, maxSize),
            getDropDownMeasureSpecMode(measureSpec)
        )
    }

    private fun getDropDownMeasureSpecSize(measureSpec: Int, maxSize: Int): Int {
        return when (measureSpec) {
            ViewGroup.LayoutParams.MATCH_PARENT -> maxSize
            else -> View.MeasureSpec.getSize(measureSpec)
        }
    }

    private fun getDropDownMeasureSpecMode(measureSpec: Int): Int {
        return when (measureSpec) {
            ViewGroup.LayoutParams.WRAP_CONTENT -> View.MeasureSpec.UNSPECIFIED
            else -> View.MeasureSpec.EXACTLY
        }
    }

}