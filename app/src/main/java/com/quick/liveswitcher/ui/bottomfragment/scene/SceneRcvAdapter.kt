package com.quick.liveswitcher.ui.bottomfragment.scene

import android.graphics.Bitmap
import android.graphics.Outline
import android.text.TextUtils
import android.transition.Transition
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.quick.liveswitcher.R
import com.quick.liveswitcher.data.scene.SceneBean
import com.quick.liveswitcher.databinding.RcvItemSceneBinding
import com.quick.liveswitcher.ui.widget.GradientColorBorderView
import com.quick.liveswitcher.utils.ResUtils
import java.io.File

class SceneRcvAdapter() : RecyclerView.Adapter<SceneRcvAdapter.SceneRcvAdapterHolder>() {

    private var mDataList: ArrayList<SceneBean> = ArrayList()
    private var mOnItemClickListener: ((SceneBean) -> Unit)? = null
    private var mOnLongClickListener: ((SceneBean) -> Unit)? = null

    fun setData(dataList: ArrayList<SceneBean>) {
        mDataList.clear()
        mDataList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun getDataList(): ArrayList<SceneBean> {
        return mDataList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SceneRcvAdapterHolder {
        val inflate = RcvItemSceneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SceneRcvAdapterHolder(inflate.root)
    }


    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(holder: SceneRcvAdapterHolder, position: Int) {
        holder.bind(mDataList[position])
    }


    inner class SceneRcvAdapterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv: ImageView? = null
        var cl_scene_root: ConstraintLayout? = null
        var cl_name: ConstraintLayout? = null
        var tv_name: TextView? = null
        var selected_flag_view: GradientColorBorderView? = null

        init {
            iv = itemView.findViewById(R.id.scene_iv)
            cl_scene_root = itemView.findViewById(R.id.cl_scene_root)
            cl_name = itemView.findViewById(R.id.cl_name)
            tv_name = itemView.findViewById(R.id.scene_tv_name)
            selected_flag_view = itemView.findViewById(R.id.scene_selected_flag_view)

            cl_scene_root?.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    // 设置圆角半径
                    val cornerRadius = ResUtils.getDimenFloatPixRes(R.dimen.dp_16) // 圆角半径，单位：px
                    outline!!.setRoundRect(0, 0, view!!.width, view!!.height, cornerRadius)
                }

            }
            cl_scene_root?.clipToOutline = true
        }

        fun bind(itemData: SceneBean) {
            if (itemData.sceneName == "scan" || itemData.sceneName == "add") {
                iv?.setImageResource(ResUtils.getDrawableByName(itemData.appIconName!!))
                cl_name?.visibility = View.GONE
            } else {
                cl_name?.visibility = View.VISIBLE
                tv_name?.text = itemData.sceneName

                val pPath = itemData.previewScreenShotIvPath;
                if (TextUtils.isEmpty(pPath)) {
                    iv?.setImageResource(R.drawable.bg_1c1c1e_rec)
                } else {
                    Glide.with(itemView.context)
                        .asBitmap()
                        .load(itemData.previewScreenShotIvPath?.let { File(it) })
                        .error(R.drawable.bg_1c1c1e_rec)
                        .into(iv!!)

//                    val requestOptions = RequestOptions()
//                        .skipMemoryCache(true)
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    Glide.with(itemView.context)
//                        .asBitmap()
//                        .load(itemData.previewScreenShotIvPath?.let { File(it) })
//                        .apply(requestOptions)
//                        .error(R.drawable.bg_1c1c1e_rec)
//                        .into(object : SimpleTarget<Bitmap>() {
//                            override fun onResourceReady(resource: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
//                                //解决glide闪烁问题
//                                iv?.setImageBitmap(resource)
//                            }
//                        })
                }


            }
            if (itemData.isSelected) {
                selected_flag_view?.visibility = View.VISIBLE
            } else {
                selected_flag_view?.visibility = View.GONE
            }
            cl_scene_root?.setOnClickListener(View.OnClickListener {
                mOnItemClickListener?.invoke(itemData)
            })
            cl_scene_root?.onLongClickListener = View.OnLongClickListener {
                mOnLongClickListener?.invoke(itemData)
                true
            }
        }
    }

    fun setOnItemClickListener(listener: (SceneBean) -> Unit) {
        mOnItemClickListener = listener
    }

    fun setOnLongClickListener(listener: (SceneBean) -> Unit) {
        mOnLongClickListener = listener
    }
}