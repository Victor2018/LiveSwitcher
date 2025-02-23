package com.quick.liveswitcher.ui.bottomfragment.picture

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.FragmentPicBinding
import com.quick.liveswitcher.interfaces.OnDialogOkCancelClickListener
import com.quick.liveswitcher.ui.dialog.LayerDeleteTipDialog

class PictureFragment : Fragment(),OnItemClickListener {
    private lateinit var binding: FragmentPicBinding

    private var mLayerDeleteTipDialog: LayerDeleteTipDialog? = null
    private lateinit var mPictureAdapter: PictureAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPicBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initData()
    }

    fun initView() {
        mPictureAdapter = PictureAdapter(context,this)
        binding.picRcv.layoutManager = GridLayoutManager(context, 5)
        binding.picRcv.addItemDecoration(PictureRcvItemSpacesDecoration())
        binding.picRcv.adapter = mPictureAdapter
    }

    private fun initData(){
        val picRcvItemDataList = ArrayList<PictureRcvItemData>()

        picRcvItemDataList.add(PictureRcvItemData("add", R.mipmap.app_icon_plus))
        picRcvItemDataList.add(PictureRcvItemData("特惠价", R.mipmap.pic_icon_1))
        picRcvItemDataList.add(PictureRcvItemData("特惠价", R.mipmap.pic_icon_1))
        picRcvItemDataList.add(PictureRcvItemData("特惠价", R.mipmap.pic_icon_1))

        mPictureAdapter.showData(picRcvItemDataList)

    }

    fun showLayerDeleteTipDialog(data: PictureRcvItemData?) {
        if (mLayerDeleteTipDialog == null) {
            mLayerDeleteTipDialog = LayerDeleteTipDialog(requireActivity())
            mLayerDeleteTipDialog?.mOnDialogOkCancelClickListener = object  :
                OnDialogOkCancelClickListener {
                override fun OnDialogOkClick() {
                }

                override fun OnDialogCancelClick() {
                }

            }
        }
        mLayerDeleteTipDialog?.mPictureRcvItemData = data
        mLayerDeleteTipDialog?.show()
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val data = mPictureAdapter.getItem(position)
        showLayerDeleteTipDialog(data)
    }

}