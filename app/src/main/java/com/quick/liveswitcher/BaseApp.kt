package com.quick.liveswitcher

import android.annotation.SuppressLint
import android.app.Application

class BaseApp : Application() {
    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: BaseApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}