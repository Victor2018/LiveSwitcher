package com.quick.liveswitcher.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.graphics.LinearGradient
import android.util.AttributeSet
import android.view.View
import com.quick.liveswitcher.R
import com.quick.liveswitcher.utils.ResUtils

class GradientColorBorderView : View {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    var mGradientColors: IntArray? = null
    val gradientPositions = floatArrayOf(0f, 0.5f, 1f)
    private var cornerRadius: Float = ResUtils.getDimenFloatPixRes(R.dimen.dp_16) // 圆角半径

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = ResUtils.getDimenFloatPixRes(R.dimen.dp_4)

        mGradientColors = intArrayOf(resources.getColor(R.color.startColor), resources.getColor(R.color.centerColor), resources.getColor(R.color.endColor))
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mGradientColors != null) {
            // 创建线性渐变
            val gradient = LinearGradient(
                0f, 0f, width.toFloat(), height.toFloat(), mGradientColors, gradientPositions, Shader.TileMode.CLAMP
            )
            paint.shader = gradient
            // 绘制带有圆角的矩形
            path.reset()
            path.addRoundRect(0f, 0f, width.toFloat(), height.toFloat(), cornerRadius, cornerRadius, Path.Direction.CW)
            canvas.drawPath(path, paint)
        }

    }
}