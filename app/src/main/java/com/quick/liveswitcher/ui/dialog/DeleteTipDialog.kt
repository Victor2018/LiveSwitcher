package com.quick.liveswitcher.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.interfaces.OnDialogOkCancelClickListener
import com.quick.liveswitcher.utils.ScreenUtils

class DeleteTipDialog(context: Context) : AbsDialog(context), View.OnClickListener {

    lateinit var mTvTip: TextView
    lateinit var mTvCancel: TextView
    lateinit var mTvConfirm: TextView

    var mOnDialogOkCancelClickListener: OnDialogOkCancelClickListener? = null

    var tip: String? = null

    override fun bindContentView() = R.layout.dlg_delete_tip

    override fun handleWindow(window: Window) {
        window.setGravity(Gravity.BOTTOM)
    }

    override fun handleLayoutParams(wlp: WindowManager.LayoutParams?) {
        wlp?.width = (ScreenUtils.getWidth(context) * 0.5).toInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initData()
    }

    fun initView() {
        mTvTip = findViewById(R.id.mTvTip)
        mTvCancel = findViewById(R.id.mTvCancel)
        mTvConfirm = findViewById(R.id.mTvConfirm)

        mTvCancel.setOnClickListener(this)
        mTvConfirm.setOnClickListener(this)
    }

    fun initData () {
        mTvTip.text = tip
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvCancel -> {
                mOnDialogOkCancelClickListener?.OnDialogCancelClick()
                dismiss()
            }
            R.id.mTvConfirm -> {
                mOnDialogOkCancelClickListener?.OnDialogOkClick()
                dismiss()
            }
        }
    }
}