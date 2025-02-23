package com.quick.liveswitcher.ui.bottomfragment.video

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.RcvItemAppPicBinding
import com.quick.liveswitcher.databinding.RcvItemVideoBinding

class VideoRcvAdapter() : RecyclerView.Adapter<VideoRcvAdapter.VideoRcvAdapterHolder>() {

    private var mDataList: ArrayList<VideoRcvItemData> = ArrayList()

    fun setData(dataList: ArrayList<VideoRcvItemData>) {
        mDataList.clear()
        mDataList.addAll(dataList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoRcvAdapterHolder {
        val inflate = RcvItemVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VideoRcvAdapterHolder(inflate.root)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: VideoRcvAdapterHolder, position: Int) {
        holder.bind(mDataList[position])
    }

    inner class VideoRcvAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv : ImageView? = null
        init {
            iv = itemView.findViewById(R.id.video_iv)
        }
        fun bind(itemData: VideoRcvItemData){
            iv?.setImageResource(itemData.icon)
        }

    }
}