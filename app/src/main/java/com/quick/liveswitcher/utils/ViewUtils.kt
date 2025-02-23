package com.quick.liveswitcher.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window

object ViewUtils {
    fun View.show() {
        visibility = View.VISIBLE
    }

    fun View.hide() {
        visibility = View.GONE
    }

    fun View.invisible() {
        visibility = View.INVISIBLE
    }

    fun getViewByLayout (context: Context?,layoutId: Int): View {
        var inflater = LayoutInflater.from(context)
        return inflater.inflate(layoutId, null)
    }

    fun getViewByLayout (inflater: LayoutInflater,layoutId: Int): View {
        return inflater.inflate(layoutId, null)
    }

    fun View.bitmap(): Bitmap {
        //不加下面两句，会报错：width and height must be > 0
        measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))

        layout(0, 0, measuredWidth, measuredHeight)

        val bitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw(canvas)
        return bitmap
    }

    fun generateViewCacheBitmap(view: View): Bitmap? {
        try {
            view.destroyDrawingCache()
            val widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            val heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            view.measure(widthMeasureSpec, heightMeasureSpec)
            val width = view.measuredWidth
            val height = view.measuredHeight
            view.layout(0, 0, width, height)
            view.isDrawingCacheEnabled = true
            view.buildDrawingCache()
            return Bitmap.createBitmap(view.drawingCache)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun generateViewFullBitmap(view: View): Bitmap? {
        try {
            view.destroyDrawingCache()
            view.isDrawingCacheEnabled = true

            view.buildDrawingCache()

            val bmp: Bitmap = view.drawingCache // 获取图片

            //解决透明边框 保存后为黑色的问题

            val newBitmap = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(newBitmap)

            canvas.drawColor(Color.WHITE)

            canvas.drawBitmap(bmp, 0f, 0f, null)

            return newBitmap

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getCurrentBitmap(window: Window): Bitmap? {
        try {
            val activityView: View = window.decorView
            activityView.destroyDrawingCache()

            val bmp: Bitmap = activityView.drawingCache // 获取图片

            //解决透明边框 保存后为黑色的问题

            val newBitmap = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)

            val canvas = Canvas(newBitmap)

            canvas.drawColor(Color.WHITE)

            canvas.drawBitmap(bmp, 0f, 0f, null)

            return newBitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun createBitmap2(v: View): Bitmap? {

        val width = v.width
        val height = v.height

        if (width * height <= 0) {
            return null
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) //准备图片

        val canvas = Canvas(bitmap) //将bitmap作为绘制画布

        v.draw(canvas) //讲View特定的区域绘制到这个canvas（bitmap）上去，

        return bitmap //得到最新的画布

    }

    fun findBrotherView(view: View, id: Int, level: Int): View? {
        var count = 0
        var temp = view
        while (count < level) {
            val target = temp.findViewById<View>(id)
            if (target != null) {
                return target
            }
            count += 1
            temp = if (temp.parent is View) {
                temp.parent as View
            } else {
                break
            }
        }
        return null
    }

    fun View.addRipple() = with(TypedValue()) {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        setBackgroundResource(resourceId)
    }

    fun View.removeRipple() {
        setBackgroundResource(0)
    }

    fun View.addCircleRipple() = with(TypedValue()) {
        context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
        setBackgroundResource(resourceId)
    }

    fun removeViewFormParent(view: View?) {
        try {
            val parent = view?.parent
            if (parent != null) {
                if (parent is ViewGroup) {
                    parent.removeView(view)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}