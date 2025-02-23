package com.quick.liveswitcher.ui.bottomfragment.picture

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.ui.base.BaseRecycleAdapter

class PictureAdapter(context: Context?, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<PictureRcvItemData, RecyclerView.ViewHolder>(context, listener) {

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PictureContentHolder(inflate(R.layout.rcv_item_app_pic, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: PictureRcvItemData?, position: Int) {
        val contentViewHolder = viewHolder as PictureContentHolder
        contentViewHolder.bindData(data)
        contentViewHolder.mOnItemClickListener = listener
    }

}