package com.quick.liveswitcher.ui.bottomfragment.app

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.data.scene.AppBean
import com.quick.liveswitcher.ui.base.BaseRecycleAdapter

class AppAdapter(context: Context?, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<AppBean, RecyclerView.ViewHolder>(context, listener) {

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AppContentHolder(inflate(R.layout.rcv_item_app_pic, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: AppBean?, position: Int) {
        val contentViewHolder = viewHolder as AppContentHolder
        contentViewHolder.bindData(data)
        contentViewHolder.mOnItemClickListener = listener
    }

}