package com.quick.liveswitcher.ui.dialog

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.data.LatestVersionData
import com.quick.liveswitcher.data.ProgressInfo
import com.quick.liveswitcher.interfaces.OnAppUpdateListener
import com.quick.liveswitcher.interfaces.OnDownloadProgressListener
import com.quick.liveswitcher.utils.DownloadModule
import com.quick.liveswitcher.utils.InstallApkUtil
import com.quick.liveswitcher.utils.PermissionHelper
import com.quick.liveswitcher.utils.ScreenUtils
import com.quick.liveswitcher.utils.ToastUtils
import com.quick.liveswitcher.utils.ViewUtils.hide
import com.quick.liveswitcher.utils.ViewUtils.invisible
import com.quick.liveswitcher.utils.ViewUtils.show
import java.io.File

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: AppUpdateDialog
 * Author: Victor
 * Date: 2022/8/18 18:01
 * Description: 
 * -----------------------------------------------------------------
 */

class AppUpdateDialog(context: Context) : AbsDialog(context), View.OnClickListener,
    OnDownloadProgressListener {

    val TAG = "AppUpdateDialog"

    var mOnAppUpdateListener: OnAppUpdateListener? = null
    var mLatestVersionData: LatestVersionData? = null

    var mProgressInfo: ProgressInfo? = null

    lateinit var mTvUpdateNow: TextView
    lateinit var mTvNewVersion: TextView
    lateinit var mTvUpdateContent: TextView
    lateinit var mTvContentLabel: TextView
    lateinit var mTvProgress: TextView
    lateinit var mPbDownloadProgress: ProgressBar

    override fun bindContentView() = R.layout.dlg_app_update

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.CENTER)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (ScreenUtils.getWidth(context) * 0.75).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData()
    }

    fun initialize() {
        setCanceledOnTouchOutside(true)
        mTvUpdateNow = findViewById(R.id.mTvUpdateNow)
        mTvNewVersion = findViewById(R.id.mTvNewVersion)
        mTvUpdateContent = findViewById(R.id.mTvUpdateContent)
        mPbDownloadProgress = findViewById(R.id.mPbDownloadProgress)
        mTvContentLabel = findViewById(R.id.mTvContentLabel)
        mTvProgress = findViewById(R.id.mTvProgress)

        mTvUpdateNow.setOnClickListener(this)
    }

    fun initData() {
        mTvNewVersion.text = mLatestVersionData?.version
        mTvUpdateContent.text = mLatestVersionData?.explain
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvUpdateNow -> {
                if (mProgressInfo != null) {
                    install(mProgressInfo?.getFile()!!)
                    return
                }
                download()
            }
        }
    }

    fun install(file: File?) {
        InstallApkUtil.install(context, file!!)
    }

    fun download() {
        mTvUpdateContent.invisible()
        mTvUpdateNow.invisible()
        mTvContentLabel.invisible()

        mPbDownloadProgress.show()
        mTvProgress.show()

        DownloadModule.instance.downloadFile(context, mLatestVersionData, this)
    }


    override fun OnDownloadProgress(data: ProgressInfo?) {
        var percent = data?.percent()?.toInt() ?: 0
        mPbDownloadProgress?.progress = percent
        mTvProgress.text = "${percent}%"
    }

    override fun OnDownloadCompleted(data: ProgressInfo?) {
        if (DownloadModule.instance.isFileExists(data)) {
            mProgressInfo = data
            mTvUpdateNow.visibility = View.VISIBLE
            mTvUpdateNow.text = "安装更新"
            install(data?.getFile())
        }
    }

    override fun OnError(error: String) {
        ToastUtils.show("下载失败：$error")
        mTvUpdateContent.show()
        mTvUpdateNow.show()
        mPbDownloadProgress.hide()
    }

}