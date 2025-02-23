package com.quick.liveswitcher.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.ImageMenuBinding

class ImageMenu: ConstraintLayout, OnCheckedChangeListener, OnSeekBarChangeListener {
    lateinit var binding: ImageMenuBinding

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }

    private fun initView (context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = ImageMenuBinding.inflate(inflater,this,true)

        binding.mToggleAutoImageCut.setOnCheckedChangeListener(this)
        binding.mSbOpacity.setOnSeekBarChangeListener(this)
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.mToggleAutoImageCut -> {
            }
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        when (seekBar?.id) {
            R.id.mSbOpacity -> {
                binding.mTvOpacity.text = "$progress%"
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }
}