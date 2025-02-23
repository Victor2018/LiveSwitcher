package com.quick.liveswitcher.ui.operatearea

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.ui.base.BaseRecycleAdapter

class TextFontAdapter(context: Context?, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<Int, RecyclerView.ViewHolder>(context, listener) {

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TextFontContentHolder(inflate(R.layout.rv_text_font_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: Int?, position: Int) {
        val contentViewHolder = viewHolder as TextFontContentHolder
        contentViewHolder.bindData(data)
        contentViewHolder.mOnItemClickListener = listener
    }

}