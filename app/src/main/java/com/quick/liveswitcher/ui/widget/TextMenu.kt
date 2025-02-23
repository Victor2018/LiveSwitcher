package com.quick.liveswitcher.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import androidx.constraintlayout.widget.ConstraintLayout
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.TextMenuBinding
import com.quick.liveswitcher.ui.dialog.RelativePopupWindow
import com.quick.liveswitcher.ui.operatearea.TextFontPopWindow
import com.quick.liveswitcher.utils.ResUtils
import com.quick.liveswitcher.utils.ToastUtils

class TextMenu: ConstraintLayout,OnClickListener {
    lateinit var binding: TextMenuBinding
    lateinit var mTextFontPopWindow: TextFontPopWindow

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {
        initView(context)
    }

    private fun initView (context: Context) {
        val inflater = LayoutInflater.from(context)
        binding = TextMenuBinding.inflate(inflater,this,true)

        binding.mTvSelectedTextFont.setOnClickListener(this)

        mTextFontPopWindow = TextFontPopWindow(context)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvSelectedTextFont -> {
                ToastUtils.show("333")
                var vertPos = RelativePopupWindow.VerticalPosition.BELOW
                var horizPos = RelativePopupWindow.HorizontalPosition.CENTER
                val yOffset = ResUtils.getDimenFloatPixRes(R.dimen.dp_10).toInt()
//                mTextFontPopWindow.showDatas(null)
//                mTextFontPopWindow.setCheckedItem(88)
                mTextFontPopWindow.showOnAnchor(v!!, vertPos,horizPos, 0,yOffset,false)
            }
        }
    }
}