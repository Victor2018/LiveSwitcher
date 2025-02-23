package com.quick.liveswitcher.ui.bottomfragment.app

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.data.scene.AppBean
import com.quick.liveswitcher.ui.base.ContentViewHolder
import com.quick.liveswitcher.utils.ResUtils

class SelectAppContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData(data: AppBean?) {
        val mIvAppIcon = itemView.findViewById<ImageView>(R.id.mIvAppIcon)
        val mTvAppName = itemView.findViewById<TextView>(R.id.mTvAppName)
        data?.let { it?.appIconName?.let { it1 -> mIvAppIcon?.setImageResource(ResUtils.getDrawableByName(it1)) } }
        mTvAppName.text = data?.appName ?: ""
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}