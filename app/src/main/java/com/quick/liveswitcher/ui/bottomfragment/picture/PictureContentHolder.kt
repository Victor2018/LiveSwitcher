package com.quick.liveswitcher.ui.bottomfragment.picture

import android.view.View
import android.widget.ImageView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.ui.base.ContentViewHolder

class PictureContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData(data: PictureRcvItemData?) {
        val mIvApp = itemView.findViewById<ImageView>(R.id.app_iv)
        data?.let { it?.icon?.let { it1 -> mIvApp?.setImageResource(it1) } }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}