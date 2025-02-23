package com.quick.liveswitcher.ui.bottomfragment.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.FragmentVideoBinding

class VideoFragment : Fragment() {
    private lateinit var binding: FragmentVideoBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentVideoBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
    }

    private fun initData(){
        val videoRcvItemDataList = ArrayList<VideoRcvItemData>()
        videoRcvItemDataList.add(VideoRcvItemData("add", R.mipmap.app_icon_plus))

        val adapter = VideoRcvAdapter()
        adapter.setData(videoRcvItemDataList)

        binding.videoRcv.layoutManager = GridLayoutManager(context, 5)
        binding.videoRcv.addItemDecoration(VideoRcvItemSpacesDecoration())
        binding.videoRcv.adapter = adapter
    }

}