package com.quick.liveswitcher.interfaces

import com.quick.liveswitcher.data.ProgressInfo

interface OnDownloadProgressListener {
    fun OnDownloadProgress(data: ProgressInfo?)
    fun OnDownloadCompleted(data: ProgressInfo?)
    fun OnError(error: String)
}