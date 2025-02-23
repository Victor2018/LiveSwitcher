package com.tripod.daobotai.virtualscreen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.RectF
import android.util.Size
import android.view.Surface
import com.quick.liveswitcher.virtualscreen.MultiScreenHelper
import com.quick.liveswitcher.virtualscreen.SystemApiUtil

class VirtualScreenHelper private constructor() {
    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { VirtualScreenHelper() }
    }


    private lateinit var context: Context

    var multiScreenHelper: MultiScreenHelper? = null

    fun init(context: Context) {
        this.context = context
        SystemApiUtil.getInstance().init(context.applicationContext)
    }

    var resolveInfo: ResolveInfo? = null

    private fun isAppInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        return try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    fun addVirtualSeat(pkn: String, surface: Surface, virtualSurfaceSize: Size, physicsSurfaceSize: Size) {
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val queryIntentActivities =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_ALL)
        for (info in queryIntentActivities) {
            val packageName = info.activityInfo.packageName
            if (pkn == packageName) {
                resolveInfo = info
                break
            }
        }

        resolveInfo?.let {
            multiScreenHelper =
                MultiScreenHelper(
                    context.applicationContext,
                    it,
                    surface,
                    virtualSurfaceSize,
                    physicsSurfaceSize,
                    null
                )
        }

    }


}