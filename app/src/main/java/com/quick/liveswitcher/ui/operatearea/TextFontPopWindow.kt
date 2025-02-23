package com.quick.liveswitcher.ui.operatearea

import android.content.Context
import android.view.View
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.quick.liveswitcher.R
import com.quick.liveswitcher.ui.dialog.RelativePopupWindow

class TextFontPopWindow(context: Context?) : RelativePopupWindow(context),
    AdapterView.OnItemClickListener {


    lateinit var mTextFontAdapter: TextFontAdapter

    override fun bindContentView() = R.layout.pop_text_font

    override fun getPopWidth() = 0

    override fun getPopHeight() = 0

    override fun initView(view: View?) {
        mTextFontAdapter = TextFontAdapter(mContext,this)
        view?.findViewById<RecyclerView>(R.id.mRvFont)?.adapter = mTextFontAdapter

        initData()
    }

    fun initData() {
        mTextFontAdapter.clear()
        for (i in 0 .. 5) {
            mTextFontAdapter.add(i)
        }
        mTextFontAdapter.notifyDataSetChanged()
    }

//    fun setCheckedItem(data: String?) {
//        mTextFontAdapter?.font = data
//        mTextFontAdapter?.notifyDataSetChanged()
//    }

//    fun showDatas(datas: List<String>?) {
//        mTextFontAdapter.showData(datas)
//    }

    override fun onItemClick(p0: AdapterView<*>?, v: View?, position: Int, id: Long) {
//        var data = mTextFontAdapter.getItem(position)
//        mTextFontAdapter.notifyDataSetChanged()

        dismiss()
    }

}