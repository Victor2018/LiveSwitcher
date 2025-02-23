package com.quick.liveswitcher.utils

import android.content.Context
import android.text.TextUtils
import com.quick.liveswitcher.data.FileInfo
import com.quick.liveswitcher.data.LatestVersionData
import com.quick.liveswitcher.data.ProgressInfo
import com.quick.liveswitcher.interfaces.OnDownloadProgressListener
import java.io.File

class DownloadModule {
    private val TAG = "DownloadModule"
    private object Holder {val instance = DownloadModule() }

    companion object {
        val  instance: DownloadModule by lazy { Holder.instance }
    }

    fun downloadFile(context: Context, data: LatestVersionData?, listener: OnDownloadProgressListener) {
        DownLoadTask(context,listener).execute(data)
    }

    fun downloadMessageFile(context: Context, data: FileInfo?, listener: OnDownloadProgressListener) {
        DownLoadFileTask(context,listener).execute(data)
    }

    fun isFileExists (data: ProgressInfo?): Boolean {
        if (data == null) return false
        if (TextUtils.isEmpty(data.filePath)) return false

        return File(data.filePath).exists()
    }
}