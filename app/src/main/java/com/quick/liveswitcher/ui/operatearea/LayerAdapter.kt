package com.quick.liveswitcher.ui.operatearea

import android.content.Context
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.data.LayerOperateItemBean
import com.quick.liveswitcher.ui.base.BaseRecycleAdapter

class LayerAdapter(context: Context?, listener: AdapterView.OnItemClickListener) :
    BaseRecycleAdapter<LayerOperateItemBean, RecyclerView.ViewHolder>(context, listener) {

    var hasLayerChecked = false

    override fun onCreateContentVHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LayerContentHolder(inflate(R.layout.rv_layer_cell, parent))
    }

    override fun onBindContentVHolder(viewHolder: RecyclerView.ViewHolder, data: LayerOperateItemBean?, position: Int) {
        val contentViewHolder = viewHolder as LayerContentHolder
        contentViewHolder.bindData(data,hasLayerChecked)
        contentViewHolder.mOnItemClickListener = listener
    }

}