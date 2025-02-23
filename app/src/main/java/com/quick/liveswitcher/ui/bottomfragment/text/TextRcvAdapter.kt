package com.quick.liveswitcher.ui.bottomfragment.text

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.RcvItemTextBinding

class TextRcvAdapter() : RecyclerView.Adapter<TextRcvAdapter.TextRcvAdapterHolder>() {

    private var mDataList: ArrayList<TextRcvItemData> = ArrayList()

    fun setData(dataList: ArrayList<TextRcvItemData>) {
        mDataList.clear()
        mDataList.addAll(dataList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextRcvAdapterHolder {
        val inflate = RcvItemTextBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TextRcvAdapterHolder(inflate.root)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: TextRcvAdapterHolder, position: Int) {
        holder.bind(mDataList[position])
    }

    inner class TextRcvAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv : ImageView? = null
        init {
            iv = itemView.findViewById(R.id.video_iv)
        }
        fun bind(itemData: TextRcvItemData){
            iv?.setImageResource(itemData.icon)
        }

    }
}