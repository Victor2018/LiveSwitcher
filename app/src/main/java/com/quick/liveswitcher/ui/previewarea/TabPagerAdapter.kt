package com.quick.liveswitcher.ui.previewarea

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TabPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var frags: ArrayList<Fragment>? = null
    var titles: Array<String>? = null

    override fun getItem(position: Int): Fragment {
        return frags?.get(position)!!
    }

    override fun getCount(): Int {
        return frags?.size ?: 0
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if (titles == null) return ""
        if (position >= titles?.size!!) return ""
        return titles?.get(position)
    }

    // 动态设置我们标题的方法
    fun setPageTitle(position: Int, title: String) {
        var count = titles?.size ?: 0
        if (position in 0 until count) {
            titles?.set(position, title)
            notifyDataSetChanged()
        }
    }

}