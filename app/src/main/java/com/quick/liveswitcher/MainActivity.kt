package com.quick.liveswitcher

import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.dongyou.cloudgame.app.RetrofitApi
import com.dongyou.common.http.requestApi
import com.quick.liveswitcher.data.CheckUpdateParm
import com.quick.liveswitcher.data.LatestVersionData
import com.quick.liveswitcher.databinding.ActivityMainBinding
import com.quick.liveswitcher.local.FileManager
import com.quick.liveswitcher.ui.dialog.AppUpdateDialog
import com.quick.liveswitcher.ui.operatearea.OperateAreaFragment
import com.quick.liveswitcher.ui.previewarea.PreviewAreaFragment
import com.quick.liveswitcher.ui.rightapparea.RightAppAreaFragment
import com.tripod.bls3588.video.Vcamera
import com.tripod.daobotai.virtualscreen.VirtualScreenHelper

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()

        Vcamera.init();
        VirtualScreenHelper.instance.init(this)

        sendCheckAppRequest()
    }

    override fun onResume() {
        super.onResume()
    }

    private fun initView() {
        FileManager.init()
        initPreviewArea()
        initOperaArea()
        initRightAppArea()
        initBottomArea()
    }

    // 初始化预览界面
    private fun initPreviewArea() {
        val previewAreaFragment = PreviewAreaFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fl_preview, previewAreaFragment)
        fragmentTransaction.commit()
    }

    //右边上边操作区域
    private fun initOperaArea() {
        val operateAreaFragment = OperateAreaFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fl_right_one, operateAreaFragment)
        fragmentTransaction.commit()
    }

    //右边中间应用区域
    private fun initRightAppArea() {
        val rightAppAreaFragment = RightAppAreaFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fl_right_two, rightAppAreaFragment)
        fragmentTransaction.commit()
    }

    // 底部操作区域
    private fun initBottomArea() {
        val mainBottomFragment = com.quick.liveswitcher.ui.bottomfragment.MainBottomFragment()
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.fl_bottom, mainBottomFragment)
        fragmentTransaction.commit()
    }

    private fun showAppUpdateDialog(data: LatestVersionData?) {

        if(TextUtils.isEmpty(data?.updateUrl)){

        }else{
            val mAppUpdateDialog = AppUpdateDialog(this)
            mAppUpdateDialog.mLatestVersionData = data
            mAppUpdateDialog.show()
        }


    }

    fun sendCheckAppRequest() {
        requestApi( {
            val contentType = "application/json"
            val body = CheckUpdateParm()
            body.versionName = getAppVersionName()
            RetrofitApi.instance.apiService.checkAppUpdate(contentType,body)
        },{
            if (it?.data != null) {
                showAppUpdateDialog(it.data)
            }
        })
    }

    fun getAppVersionName(): String {
        val packageName = packageName ?: ""
        try {
            return packageManager?.getPackageInfo(
                packageName, 0)?.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            throw RuntimeException("System fault", e)
        }

    }

}