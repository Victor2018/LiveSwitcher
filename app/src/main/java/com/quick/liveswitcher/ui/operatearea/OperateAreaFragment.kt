package com.quick.liveswitcher.ui.operatearea

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.FragmentOperaAreaBinding
import com.quick.liveswitcher.db.DBInjector
import com.quick.liveswitcher.db.entity.PreViewLayerEntity
import com.quick.liveswitcher.db.vm.PreViewLayerVM
import com.quick.liveswitcher.ui.previewarea.TabPagerAdapter
import com.quick.liveswitcher.utils.ResUtils

class OperateAreaFragment: Fragment()  {

    lateinit var binding: FragmentOperaAreaBinding

    private val preViewLayerVM: PreViewLayerVM by viewModels {
        DBInjector.providePreViewLayerVMFactory(this, 0)
    }

    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: ArrayList<Fragment> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOperaAreaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeUi()
        initView()
    }

    fun initView() {
        pagerTitles = ResUtils.getStringArrayRes(R.array.opera_area_tab_titles)

        fragmentList.clear()
        fragmentList.add(VideoOperaFragment.newInstance())
        fragmentList.add(AudioOperaFragment.newInstance())

        mTabPagerAdapter = TabPagerAdapter(childFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        binding.vpOperaArea.adapter = mTabPagerAdapter
        binding.tabOperaArea.setupWithViewPager(binding.vpOperaArea)
    }

    fun subscribeUi() {
        preViewLayerVM.allLayerData.observe(viewLifecycleOwner, Observer {
//            Toast.makeText(context,"$it",Toast.LENGTH_SHORT).show()
        })
    }

    fun addPreViewLayer() {
        val data = PreViewLayerEntity(1,"图片","/sdcard/test.png",0)
        preViewLayerVM.insert(data)
    }
}