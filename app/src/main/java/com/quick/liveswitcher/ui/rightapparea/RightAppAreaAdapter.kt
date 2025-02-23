package com.quick.liveswitcher.ui.rightapparea

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.ui.base.BaseRecycleAdapter

class RightAppAreaAdapter(context: Context?, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<RightAppItemData, RecyclerView.ViewHolder>(context, listener) {

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return RightAppAreaContentHolder(inflate(R.layout.rv_right_app_area_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: RightAppItemData?, position: Int) {
        val contentViewHolder = viewHolder as RightAppAreaContentHolder
        contentViewHolder.bindData(data)
        contentViewHolder.mOnItemClickListener = listener
    }

}