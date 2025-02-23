package com.quick.liveswitcher.ui.bottomfragment.text

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.FragmentTextBinding

class TextFragment : Fragment() {
    private lateinit var binding: FragmentTextBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentTextBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData(){
        val textRcvItemDataList = ArrayList<TextRcvItemData>()

        textRcvItemDataList.add(TextRcvItemData("add", R.mipmap.app_icon_plus))
        textRcvItemDataList.add(TextRcvItemData("超值特惠", R.mipmap.text_icon_czth))
        textRcvItemDataList.add(TextRcvItemData("空白", R.mipmap.text_icon_black))

        val adapter = TextRcvAdapter()
        adapter.setData(textRcvItemDataList)

        binding.textRcv.layoutManager = GridLayoutManager(context, 5)
        binding.textRcv.addItemDecoration(TextRcvItemSpacesDecoration())
        binding.textRcv.adapter = adapter
    }

}