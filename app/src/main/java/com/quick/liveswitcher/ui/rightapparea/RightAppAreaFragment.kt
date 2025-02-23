package com.quick.liveswitcher.ui.rightapparea

import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import com.quick.liveswitcher.databinding.FragmentRightAppAreaBinding


class RightAppAreaFragment : Fragment(), OnItemClickListener {

    lateinit var binding: FragmentRightAppAreaBinding
    lateinit var mRightAppAreaAdapter: RightAppAreaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRightAppAreaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    fun initView() {
        mRightAppAreaAdapter = RightAppAreaAdapter(context, this)
        binding.rvRightAppArea.adapter = mRightAppAreaAdapter
    }

    fun initData() {
        mRightAppAreaAdapter?.clear()
        val douyinData = RightAppItemData()
        douyinData.appName = "抖音"
        douyinData.appPackageName = "com.ss.android.ugc.aweme"
        douyinData.appIconName = "ic_douyin"
        mRightAppAreaAdapter?.add(douyinData)

        val kuaishouData = RightAppItemData()
        kuaishouData.appName = "快手"
        kuaishouData.appPackageName = "com.kuaishou.nebula"
        kuaishouData.appIconName = "ic_kuaishou"
        mRightAppAreaAdapter?.add(kuaishouData)

        mRightAppAreaAdapter?.notifyDataSetChanged()
    }

    override fun onItemClick(p0: AdapterView<*>?, v: View?, position: Int, p3: Long) {
        val data = mRightAppAreaAdapter.getItem(position)
        openApp(data!!.appPackageName)
    }


    private fun openApp(packageName: String?) {
        // 获取所有已安装的应用包信息
        val installedPackages: List<PackageInfo> = this.context!!.packageManager.getInstalledPackages(0)

        for (item in installedPackages) {
            if (item.packageName == packageName) {
                val intent: Intent = this.context!!.packageManager.getLaunchIntentForPackage(packageName)
                startActivity(intent)
                return
            }
        }

        // 应用汇的包名
        val marketPackageName = "com.yingyonghui.market"
        // 创建Intent
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse("market://details?id=$packageName"))
        intent.setPackage(marketPackageName)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }


}