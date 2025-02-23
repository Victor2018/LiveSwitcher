package com.quick.liveswitcher.utils

import androidx.annotation.DimenRes
import com.quick.liveswitcher.BaseApp

fun getApplication(): BaseApp {
    return BaseApp.instance
}

fun getDimenPixelSize(@DimenRes id: Int): Int {
    return getApplication().resources.getDimensionPixelSize(id)
}