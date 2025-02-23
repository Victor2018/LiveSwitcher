package com.quick.liveswitcher.utils

import android.content.Context
import android.text.TextUtils
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntegerRes
import com.quick.liveswitcher.BaseApp

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: ToastUtils.java
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 吐司工具类
 * -----------------------------------------------------------------
 */

object ToastUtils {

    /**
     * 短暂显示
     *
     * @param msg
     */
    fun show(msg: CharSequence) {
        Toast.makeText(BaseApp.instance, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * 短暂显示
     *
     * @param resId
     */
    fun show(resId: Int) {
        val text = ResUtils.getStringRes(resId)
        Toast.makeText(BaseApp.instance, text, Toast.LENGTH_SHORT).show()
    }

    /**
     * 长时间显示
     *
     * @param msg
     */
    fun showLong(msg: CharSequence) {
        Toast.makeText(BaseApp.instance, msg, Toast.LENGTH_LONG).show()
    }

    /**
     * 短暂显示
     *
     * @param resId
     */
    fun showLong(resId: Int) {
        val text = ResUtils.getStringRes(resId)
        Toast.makeText(BaseApp.instance, text, Toast.LENGTH_LONG).show()
    }



}