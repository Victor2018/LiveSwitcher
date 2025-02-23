package com.quick.liveswitcher.utils

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.quick.liveswitcher.data.FileInfo
import com.quick.liveswitcher.data.ProgressInfo
import com.quick.liveswitcher.interfaces.OnDownloadProgressListener
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.net.URLConnection

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DownLoadTask
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

class DownLoadFileTask(var context: Context?, var listener: OnDownloadProgressListener?)
    : AsyncTask<FileInfo, Integer, ProgressInfo>() {
    val TAG = "DownLoadFileTask"
    var data = ProgressInfo()

    override fun doInBackground(vararg params: FileInfo?): ProgressInfo {
        try {
            var updateData = params[0]

            data.downloadUrl = updateData?.url
            data.savePath = context?.filesDir?.path
            data.filePath = data.savePath + File.separator + data.getFileNameByUrl()
            var url = URL(data.downloadUrl)

            val con: URLConnection = url.openConnection()
            data.totalSize = con.contentLength.toLong()
            val inputStream = con.getInputStream()

            val file = File(data.filePath)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()

            val fos = FileOutputStream(file)
            val buffer = ByteArray(2048)
            var size: Int = inputStream.read(buffer)
            while (size != -1) {
                data.downloadSize += size
                fos.write(buffer, 0, size)
                size = inputStream.read(buffer)
                //    时刻将当前进度更新给onProgressUpdate方法
                publishProgress(Integer(data?.percent()?.toInt() ?: 0))
            }
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
            MainHandler.get().runMainThread(Runnable {
                listener?.OnError(e.localizedMessage)
            })
        }

        return data
    }

    override fun onProgressUpdate(vararg values: Integer?) {
        super.onProgressUpdate(*values)
        Log.e(TAG, "download-progress = " + values[0] + "%")
        listener?.OnDownloadProgress(data)
    }

    override fun onPostExecute(result: ProgressInfo) {
        super.onPostExecute(result)
        listener?.OnDownloadCompleted(result)
    }


}