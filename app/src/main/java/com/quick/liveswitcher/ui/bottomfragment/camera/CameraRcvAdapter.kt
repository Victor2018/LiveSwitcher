package com.quick.liveswitcher.ui.bottomfragment.camera

import android.annotation.SuppressLint
import android.graphics.Outline
import android.graphics.SurfaceTexture
import android.util.Log
import android.view.LayoutInflater
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.RcvItemCameraBinding
import com.quick.liveswitcher.utils.ResUtils

class CameraRcvAdapter() : RecyclerView.Adapter<CameraRcvAdapter.CameraRcvAdapterHolder>() {

    private var mDataList: ArrayList<CameraRcvItemData> = ArrayList()
    var mOnItemClickListener: OnItemClickListener? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dataList: ArrayList<CameraRcvItemData>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraRcvAdapterHolder {
        val inflate = RcvItemCameraBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CameraRcvAdapterHolder(inflate.root)
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: CameraRcvAdapterHolder, position: Int) {
        holder.bind(mDataList[position])
    }

    inner class CameraRcvAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv: ImageView? = null
        var camera_texture_view: TextureView? = null
        var cameraSurface: Surface? = null
        var mItemData: CameraRcvItemData? = null
        var camera_selected_flag_view: View? = null


        init {
            iv = itemView.findViewById(R.id.camera_iv)
            camera_texture_view = itemView.findViewById(R.id.camera_texture_view)
            camera_selected_flag_view = itemView.findViewById(R.id.camera_selected_flag_view)
            itemView.setOnClickListener {
                mOnItemClickListener?.onItemClick(null, itemView, adapterPosition, 0)
            }
            camera_texture_view?.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture?, width: Int, height: Int) {
                    cameraSurface = Surface(surface)
                    Log.d("tag", "onSurfaceTextureAvailable")
                    if (mItemData != null && mItemData?.cameraLayer != null) {
                        mItemData?.cameraLayer?.addExtraOutput("btm_camera_preview", cameraSurface)
                        camera_selected_flag_view?.visibility = View.VISIBLE
                    }
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture?, width: Int, height: Int) {

                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                    return false
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {

                }

            }

            camera_texture_view?.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    // 设置圆角半径
                    val cornerRadius = ResUtils.getDimenFloatPixRes(R.dimen.dp_16) // 圆角半径，单位：px
                    outline!!.setRoundRect(0, 0, view!!.width, view!!.height, cornerRadius)
                }

            }
            camera_texture_view?.clipToOutline = true

        }

        fun bind(itemData: CameraRcvItemData) {
            mItemData = itemData
            iv?.setImageResource(itemData.icon ?: 0)
            if (itemData.cameraLayer != null) {
                iv?.visibility = View.GONE
                camera_texture_view?.visibility = View.VISIBLE
                if (cameraSurface != null) {
                    itemData.cameraLayer?.addExtraOutput("btm_camera_preview", cameraSurface)
                    camera_selected_flag_view?.visibility = View.VISIBLE
                }
            } else {
                iv?.visibility = View.VISIBLE
                camera_selected_flag_view?.visibility = View.GONE
                camera_texture_view?.visibility = View.GONE
            }
        }

    }
}