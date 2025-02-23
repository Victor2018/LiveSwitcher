package com.quick.liveswitcher.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.interfaces.OnDialogOkCancelClickListener
import com.quick.liveswitcher.ui.bottomfragment.picture.PictureRcvItemData
import com.quick.liveswitcher.utils.ScreenUtils

class LayerDeleteTipDialog(context: Context) : AbsDialog(context), View.OnClickListener {

    lateinit var mIvClose: ImageView
    lateinit var mIvLayerIcon: ImageView
    lateinit var mTvCancel: TextView
    lateinit var mTvDelete: TextView

    var mOnDialogOkCancelClickListener: OnDialogOkCancelClickListener? = null

    var mPictureRcvItemData: PictureRcvItemData? = null

    override fun bindContentView() = R.layout.dlg_layer_delete_tip

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.BOTTOM)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (ScreenUtils.getWidth(context) * 0.68).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    fun initView() {
        mIvClose = findViewById(R.id.mIvClose)
        mIvLayerIcon = findViewById(R.id.mIvLayerIcon)
        mTvCancel = findViewById(R.id.mTvCancel)
        mTvDelete = findViewById(R.id.mTvDelete)

        mIvClose.setOnClickListener(this)
        mTvCancel.setOnClickListener(this)
        mTvDelete.setOnClickListener(this)
    }

    fun initData () {
        mPictureRcvItemData?.icon?.let { mIvLayerIcon.setImageResource(it) }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mIvClose -> {
                dismiss()
            }
            R.id.mTvCancel -> {
                mOnDialogOkCancelClickListener?.OnDialogCancelClick()
                dismiss()
            }
            R.id.mTvDelete -> {
                mOnDialogOkCancelClickListener?.OnDialogOkClick()
                dismiss()
            }
        }
    }
}