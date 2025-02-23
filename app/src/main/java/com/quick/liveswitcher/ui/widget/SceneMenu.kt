package com.quick.liveswitcher.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.quick.liveswitcher.databinding.SceneMenuBinding

class SceneMenu: ConstraintLayout {

    lateinit var binding: SceneMenuBinding
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }

    private fun initView (context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = SceneMenuBinding.inflate(inflater,this,true)
    }
}