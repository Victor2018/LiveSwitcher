package com.quick.liveswitcher.ui.bottomfragment.app

import android.view.View
import android.widget.ImageView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.data.scene.AppBean
import com.quick.liveswitcher.ui.base.ContentViewHolder
import com.quick.liveswitcher.utils.ResUtils

class AppContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData(data: AppBean?) {
        val mIvAppIcon = itemView.findViewById<ImageView>(R.id.app_iv)
        val mViewSelected = itemView.findViewById<View>(R.id.app_selected_flag_view)
        data?.let {
            it.appIconName?.let { it1 -> mIvAppIcon?.setImageResource(ResUtils.getDrawableByName(it1)) }
            mViewSelected.visibility = if (it.isSelected) View.VISIBLE else View.GONE
        }
    }

    override fun onLongClick(v: View): Boolean {
        mOnItemClickListener?.onItemClick(null, v, adapterPosition, ONITEM_LONG_CLICK)
        return true
    }
}