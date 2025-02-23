package com.quick.liveswitcher.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.data.scene.AppBean
import com.quick.liveswitcher.interfaces.OnAppSelectListener
import com.quick.liveswitcher.ui.bottomfragment.app.SelectAppAdapter
import com.quick.liveswitcher.utils.ScreenUtils

class AppSelectDialog(context: Context) : AbsDialog(context), View.OnClickListener,OnItemClickListener {

    lateinit var mIvClose: ImageView
    lateinit var mRvApp: RecyclerView
    lateinit var mSelectAppAdapter: SelectAppAdapter

    var mOnAppSelectListener: OnAppSelectListener? = null

    override fun bindContentView() = R.layout.dlg_app_select

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.BOTTOM)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = ScreenUtils.getWidth(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    fun initView() {
        mIvClose = findViewById(R.id.mIvClose)
        mRvApp = findViewById(R.id.mRvApp)

        mSelectAppAdapter = SelectAppAdapter(context,this)
        mRvApp.adapter = mSelectAppAdapter

        mIvClose.setOnClickListener(this)
    }

    fun initData () {
        mSelectAppAdapter.clear()
        val bean1 = AppBean()
        bean1.appName = "算粒智播"
        bean1.appIconName = "app_icon_slzb"
        bean1.appPackageName = "com.zmwan.live"
        mSelectAppAdapter.add(bean1)

        val bean2 = AppBean()
        bean2.appName = "网易云音乐"
        bean2.appIconName = "app_icon_wangyiyun"
        bean2.appPackageName = "com.netease.cloudmusic"
        mSelectAppAdapter.add(bean2)

        mSelectAppAdapter.notifyDataSetChanged()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvClose -> {
                dismiss()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val data = mSelectAppAdapter.getItem(position)
        mOnAppSelectListener?.OnAppSelect(data)
    }
}