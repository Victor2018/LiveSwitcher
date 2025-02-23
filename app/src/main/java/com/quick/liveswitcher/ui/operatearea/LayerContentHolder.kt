package com.quick.liveswitcher.ui.operatearea

import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.quick.liveswitcher.BaseApp
import com.quick.liveswitcher.R
import com.quick.liveswitcher.data.LayerOperateItemBean
import com.quick.liveswitcher.ui.base.ContentViewHolder
import com.quick.liveswitcher.ui.bottomfragment.scene.SceneDataManager
import com.quick.liveswitcher.ui.previewarea.LayerController
import com.quick.liveswitcher.utils.ResUtils
import com.quick.liveswitcher.utils.ResUtils.getDrawableByName
import com.quick.liveswitcher.utils.ToastUtils
import com.quick.liveswitcher.utils.ViewUtils.hide
import com.quick.liveswitcher.utils.ViewUtils.show
import java.io.File

class LayerContentHolder(itemView: View) : ContentViewHolder(itemView) {

    fun bindData(data: LayerOperateItemBean?, hasLayerChecked: Boolean) {
        val mIvRemoveLayer = itemView.findViewById<ImageView>(R.id.mIvRemoveLayer)
        val mIvLayerIcon = itemView.findViewById<ImageView>(R.id.mIvLayerIcon)
        val mTvLayerName = itemView.findViewById<TextView>(R.id.mTvLayerName)

        if (hasLayerChecked) {
            mIvRemoveLayer.hide()
            mTvLayerName.hide()
        } else {
            mIvRemoveLayer.show()
            mTvLayerName.show()
        }
        //1: 图片，2：相机，3：视频，4：文字，5：APP
        when (data?.layerType) {
            1 -> {
                mTvLayerName.setText("图片图层")
                Glide.with(BaseApp.instance).load(File(data.imgPath)).into(mIvLayerIcon)
            }
            2 -> {
                mTvLayerName.setText("相机图层")
                mIvLayerIcon.setImageResource(ResUtils.getDrawableByName(data.drawableName))
            }
            3 -> {
                mTvLayerName.setText("视频图层")
            }
            4 -> { // 文字
                mTvLayerName.setText("文字图层")
            }
            5 -> { // App
                mTvLayerName.setText(data.name)
                mIvLayerIcon.setImageResource(ResUtils.getDrawableByName(data.drawableName))
            }
        }

        mIvRemoveLayer.setOnClickListener {
            if (SceneDataManager.isScanMode) {
                ToastUtils.show(R.string.current_is_scan)
            }else{
                val layerId = data?.layerId
                if(layerId != null){
                    LayerController.removeLayer(layerId)
                }
            }
        }

    }

    override fun onLongClick(v: View): Boolean {
        return false
    }
}