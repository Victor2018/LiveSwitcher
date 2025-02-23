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
import com.quick.liveswitcher.databinding.AppMenuBinding
import com.quick.liveswitcher.ui.previewarea.LayerController

class AppMenu: ConstraintLayout,OnCheckedChangeListener,OnSeekBarChangeListener, View.OnClickListener {

    lateinit var binding: AppMenuBinding
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }

    private fun initView (context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = AppMenuBinding.inflate(inflater,this,true)

        binding.mToggleGreenCut.setOnCheckedChangeListener(this)
        binding.mSbOpacity.setOnSeekBarChangeListener(this)
        binding.mSbAppVol.setOnSeekBarChangeListener(this)

        binding.clGreenCut.setOnClickListener(this)
        binding.clMirrorFlip.setOnClickListener(this)
        binding.llRotate.setOnClickListener(this)
        binding.llZoomIn.setOnClickListener(this)
        binding.llZoomOut.setOnClickListener(this)
        binding.llFullScreen.setOnClickListener(this)
        binding.llDown.setOnClickListener(this)
        binding.llUp.setOnClickListener(this)
        binding.llLeft.setOnClickListener(this)
        binding.llRight.setOnClickListener(this)
        binding.llLayerTop.setOnClickListener(this)
        binding.llLayerBottom.setOnClickListener(this)
        binding.llLayerUp.setOnClickListener(this)
        binding.llLayerDown.setOnClickListener(this)
        binding.ivDelete.setOnClickListener(this)
    }

    //当前被选中的图层
    public fun onSelectedLayer() {
        //自动抠像
        binding.mToggleGreenCut.isChecked = LayerController.getSelectedLayerIsCutBg()
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mToggleGreenCut -> {
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
            R.id.mSbAppVol -> {
                binding.mTvAppVol.text = "$progress%"
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cl_green_cut -> { //绿幕抠像
                binding.mToggleGreenCut.isChecked = !binding.mToggleGreenCut.isChecked
                LayerController.setSelectedVirtualBackground(binding.mToggleGreenCut.isChecked)
            }
            R.id.cl_mirror_flip -> { //镜像翻转
                LayerController.setSelectedHoriMirror()
            }
            R.id.ll_rotate -> { //旋转
                LayerController.setSelectedRightRotate()
            }
            R.id.ll_zoom_in -> { //放大
                LayerController.setSelectedZoomIn()
            }
            R.id.ll_zoom_out -> { //缩小
                LayerController.setSelectedZoomOut()
            }
            R.id.ll_full_screen -> { //全屏
                LayerController.setSelectedFullScreen()
            }
            R.id.ll_down -> { //下移
                LayerController.setSelectedDown()
            }
            R.id.ll_up -> { //上移
                LayerController.setSelectedUp()
            }
            R.id.ll_left -> { //左移
                LayerController.setSelectedLeft()
            }
            R.id.ll_right -> { //右移
                LayerController.setSelectedRight()
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
        }
    }


    private var mDeleteIconClickListener: OnClickListener? = null
    fun setDeleteIconClickListener(listener: OnClickListener?) {
          mDeleteIconClickListener = listener
    }


}