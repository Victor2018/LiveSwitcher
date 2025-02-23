package com.quick.liveswitcher.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.quick.liveswitcher.databinding.VideoOperaMenuBinding
import com.quick.liveswitcher.ui.previewarea.LayerController
import com.quick.liveswitcher.utils.ViewUtils.hide
import com.quick.liveswitcher.utils.ViewUtils.show
import sdk.smartx.director.layer.IRenderLayer

class VideoOperaMenu: ConstraintLayout {
    lateinit var binding: VideoOperaMenuBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }

    private fun initView (context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = VideoOperaMenuBinding.inflate(inflater,this,true)
    }

    //选中了某个图层
    fun onSelectedLayer(layerId: Int){
        val layer:IRenderLayer? = LayerController.getLayerById(layerId)
        when(layer?.config?.type){
            sdk.smartx.director.layer.LayerType.Surface->{//App
                binding.mSceneMenu.hide()
                binding.mAppMenu.show()
                binding.mImageMenu.hide()
                binding.mVideoMenu.hide()
                binding.mTextMenu.hide()
                binding.mCameraMenu.hide()
                binding.mAppMenu.onSelectedLayer()
            }
            sdk.smartx.director.layer.LayerType.Camera->{//相机
                binding.mSceneMenu.hide()
                binding.mAppMenu.hide()
                binding.mImageMenu.hide()
                binding.mVideoMenu.hide()
                binding.mTextMenu.hide()
                binding.mCameraMenu.show()
                binding.mCameraMenu.onSelectedLayer()
            }
            sdk.smartx.director.layer.LayerType.Picture->{//图片
                binding.mSceneMenu.hide()
                binding.mAppMenu.hide()
                binding.mImageMenu.show()
                binding.mVideoMenu.hide()
                binding.mTextMenu.hide()
                binding.mCameraMenu.hide()
            }
            sdk.smartx.director.layer.LayerType.Media->{//视频
                binding.mSceneMenu.hide()
                binding.mAppMenu.hide()
                binding.mImageMenu.hide()
                binding.mVideoMenu.show()
                binding.mTextMenu.hide()
                binding.mCameraMenu.hide()
            }
            sdk.smartx.director.layer.LayerType.Text->{//文字
                binding.mSceneMenu.hide()
                binding.mAppMenu.hide()
                binding.mImageMenu.hide()
                binding.mVideoMenu.hide()
                binding.mTextMenu.show()
                binding.mCameraMenu.hide()
            }
            else->{

            }
        }
    }

    // 未选中图层 //场景
    fun onUnSelectedLayer(){
        binding.mSceneMenu.show()
        binding.mAppMenu.hide()
        binding.mImageMenu.hide()
        binding.mVideoMenu.hide()
        binding.mTextMenu.hide()
        binding.mCameraMenu.hide()
    }

    fun setDeleteIconClickListener(listener: OnClickListener?) {
          binding.mAppMenu.setDeleteIconClickListener(listener)
          binding.mCameraMenu.setDeleteIconClickListener(listener)
    }

}