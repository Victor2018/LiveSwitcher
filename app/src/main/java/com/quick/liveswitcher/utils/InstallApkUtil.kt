package com.quick.liveswitcher.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

object InstallApkUtil {
    fun install (context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val packageName = context.packageName
        val authority = "$packageName.fileProvider"
        val uri = FileProvider.getUriForFile(context, authority, file)
//        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            FileProvider.getUriForFile(context, authority, file)
//        } else {
//            Uri.fromFile(file)
//        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context?.startActivity(intent)
    }
}