package com.quick.liveswitcher.ui.bottomfragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.quick.liveswitcher.R
import com.quick.liveswitcher.action.EventAction
import com.quick.liveswitcher.databinding.FragmentMainbottomBinding
import com.quick.liveswitcher.ui.bottomfragment.app.AppFragment
import com.quick.liveswitcher.ui.bottomfragment.camera.CameraFragment
import com.quick.liveswitcher.ui.bottomfragment.scene.SceneFragment
import com.quick.liveswitcher.ui.previewarea.TabPagerAdapter
import com.quick.liveswitcher.utils.ResUtils
import com.quick.liveswitcher.livedatabus.LiveDataBus

class MainBottomFragment : Fragment(),OnPageChangeListener {
    private lateinit var binding: FragmentMainbottomBinding
    var mTabPagerAdapter: TabPagerAdapter? = null
    private var pagerTitles: Array<String>? = null
    private var fragmentList: ArrayList<Fragment> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainbottomBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    private fun initView() {
        pagerTitles = ResUtils.getStringArrayRes(R.array.bottom_area_tab_titles)

        fragmentList.clear()
        fragmentList.add(SceneFragment())
        fragmentList.add(AppFragment())
//        fragmentList.add(PictureFragment())
//        fragmentList.add(VideoFragment())
//        fragmentList.add(TextFragment())
        fragmentList.add(CameraFragment())

        mTabPagerAdapter = TabPagerAdapter(childFragmentManager)
        mTabPagerAdapter?.titles = pagerTitles
        mTabPagerAdapter?.frags = fragmentList
        binding.vpBottomArea.adapter = mTabPagerAdapter
        binding.vpBottomArea.canScroll = true
        binding.vpBottomArea.addOnPageChangeListener(this)
        binding.tabBottomArea.setupWithViewPager(binding.vpBottomArea)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        LiveDataBus.sendMulti(EventAction.TAB_CHANGED,position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }
}