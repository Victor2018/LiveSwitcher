package com.quick.liveswitcher.ui.widget

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R

class MaxHeightRecyclerView : RecyclerView {
    private var maxHeight = -1

    constructor(context: Context) : this(context,null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs,0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context,attrs,defStyleAttr)
    }

    // Modified changes
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a: TypedArray = context.obtainStyledAttributes(
            attrs, R.styleable.MaxHeightRecyclerView, defStyleAttr, 0)
        maxHeight = a.getDimensionPixelSize(R.styleable.MaxHeightRecyclerView_maxHeight, 0)
        a.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpec = heightMeasureSpec
        if (maxHeight > 0) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun setMaxHeight (height: Int) {
        maxHeight = height
        requestLayout()
    }
}