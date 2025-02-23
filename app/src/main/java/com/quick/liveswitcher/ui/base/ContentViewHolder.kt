package com.quick.liveswitcher.ui.base

import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView

open class ContentViewHolder: RecyclerView.ViewHolder,View.OnClickListener,View.OnLongClickListener {
    companion object {
        const val ONITEM_LONG_CLICK: Long = -1
        const val ONITEM_CLICK: Long = 0
    }

    var mOnItemClickListener: AdapterView.OnItemClickListener? = null

    fun setOnItemClickListener(listener: AdapterView.OnItemClickListener?) {
        mOnItemClickListener = listener
    }

    constructor(itemView: View) : super(itemView) {
        itemView.setOnClickListener(this)
        itemView.setOnLongClickListener(this)
    }

    override fun onClick(view: View) {
        mOnItemClickListener?.onItemClick(null, view, adapterPosition, ONITEM_CLICK)
    }

    override fun onLongClick(v: View): Boolean {
        mOnItemClickListener?.onItemClick(null, v, adapterPosition, ONITEM_LONG_CLICK)
        return false
    }
}