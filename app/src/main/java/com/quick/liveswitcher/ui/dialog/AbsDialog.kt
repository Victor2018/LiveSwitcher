package com.quick.liveswitcher.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.quick.liveswitcher.R

abstract class AbsDialog(context: Context): Dialog(context, R.style.BaseNoTitleDialog) {

    protected abstract fun bindContentView(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bindContentView())

        //设置属性信息宽高或者动画
        handleWindow(window!!)
        val wlp = window!!.attributes
        handleLayoutParams(wlp)
        window!!.attributes = wlp

    }

    /**
     * 用于处理窗口的属性
     *
     * @param window
     */
    abstract fun handleWindow(window: Window)

    abstract fun handleLayoutParams(wlp: WindowManager.LayoutParams?)


}