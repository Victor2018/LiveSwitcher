package com.quick.liveswitcher.ui.rightapparea

import android.view.View
import android.widget.ImageView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.ui.base.ContentViewHolder
import com.quick.liveswitcher.utils.ResUtils

class RightAppAreaContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData(data: RightAppItemData?) {
        val iv = itemView.findViewById<ImageView>(R.id.mIvRightApp)
        data?.let {
            it.appIconName?.let { it1 -> iv?.setImageResource(ResUtils.getDrawableByName(it1)) }
        }
    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}