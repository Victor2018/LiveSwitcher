package com.quick.liveswitcher.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.CameraMenuBinding
import com.quick.liveswitcher.ui.previewarea.LayerController

class CameraMenu : ConstraintLayout, OnCheckedChangeListener, OnSeekBarChangeListener,
    View.OnClickListener {

    lateinit var binding: CameraMenuBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        initView(context)
    }

    private fun initView(context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = CameraMenuBinding.inflate(inflater, this, true)

        binding.mToggleCamera.setOnCheckedChangeListener(this)
        binding.mToggleAutoImageCut.setOnCheckedChangeListener(this)
        binding.mSbOpacity.setOnSeekBarChangeListener(this)

        binding.mIvMirrorFlip.setOnClickListener(this)
        binding.mIvLeftRotate.setOnClickListener(this)
        binding.mIvRightRotate.setOnClickListener(this)
        binding.clToggleCamera.setOnClickListener(this)
        binding.clAutoImageCut.setOnClickListener(this)
        binding.llLayerTop.setOnClickListener(this)
        binding.llLayerBottom.setOnClickListener(this)
        binding.llLayerUp.setOnClickListener(this)
        binding.llLayerDown.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)
        binding.llRotate.setOnClickListener(this)
        binding.llFullScreen.setOnClickListener(this)
    }

    //当前被选中的图层
    public fun onSelectedLayer() {
        //相机开关
        binding.mToggleCamera.isChecked = LayerController.getSelectedLayerVisible()
        //自动抠像
        binding.mToggleAutoImageCut.isChecked = LayerController.getSelectedLayerIsCutBg()
    }

    public fun reset(){
        //相机开关
        binding.mToggleCamera.isChecked = false;
        //自动抠像
        binding.mToggleAutoImageCut.isChecked = false;
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mToggleCamera -> {//相机开关
                LayerController.setSelectedLayerVisible(isChecked)
            }
            R.id.mToggleAutoImageCut -> {//自动抠像
                LayerController.setSelectedVirtualBackground(isChecked)
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.mSbOpacity -> {
                binding.mTvOpacity.text = "$progress%"
                LayerController.setSelectedAlpha(progress / 100f)
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.mIvMirrorFlip -> { //镜像翻转
                LayerController.setSelectedHoriMirror()
            }
            R.id.mIvLeftRotate -> { //左旋转
                LayerController.setSelectedLeftRotate()
            }
            R.id.mIvRightRotate -> { //右旋转
                LayerController.setSelectedRightRotate()
            }
            R.id.cl_toggle_camera -> { //相机开关
                binding.mToggleCamera.isChecked = !binding.mToggleCamera.isChecked
                LayerController.setSelectedLayerVisible(binding.mToggleCamera.isChecked)
            }
            R.id.cl_auto_image_cut -> { //自动抠像
                binding.mToggleAutoImageCut.isChecked = !binding.mToggleAutoImageCut.isChecked
                LayerController.setSelectedVirtualBackground(binding.mToggleAutoImageCut.isChecked)
            }
            R.id.ll_layer_top -> { //置顶
                LayerController.setSelectedToTopLayer()
            }
            R.id.ll_layer_bottom -> { //置底
                LayerController.setSelectedToBottomLayer()
            }
            R.id.ll_layer_up -> { //上移一层
                LayerController.setSelectedForwardLayer()
            }
            R.id.ll_layer_down -> { //下移一层
                LayerController.setSelectedBackLayer()
            }
            R.id.iv_delete -> { //删除
                mDeleteIconClickListener?.onClick(view)
            }
            R.id.ll_rotate -> { //旋转
                LayerController.setSelectedRightRotate()
            }
            R.id.ll_full_screen -> { //全屏
                LayerController.setSelectedFullScreen()
            }
        }
    }

    private var mDeleteIconClickListener: OnClickListener? = null
    fun setDeleteIconClickListener(listener: OnClickListener?) {
        mDeleteIconClickListener = listener
    }

}