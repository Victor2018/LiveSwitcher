package com.quick.liveswitcher.ui.bottomfragment.app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.quick.liveswitcher.R
import com.quick.liveswitcher.action.EventAction
import com.quick.liveswitcher.data.scene.AppBean
import com.quick.liveswitcher.databinding.FragmentAppBinding
import com.quick.liveswitcher.interfaces.OnAppSelectListener
import com.quick.liveswitcher.interfaces.OnDialogOkCancelClickListener
import com.quick.liveswitcher.ui.bottomfragment.scene.SceneDataManager
import com.quick.liveswitcher.ui.dialog.AppSelectDialog
import com.quick.liveswitcher.ui.previewarea.LayerController
import com.quick.liveswitcher.livedatabus.LiveDataBus
import com.quick.liveswitcher.ui.base.ContentViewHolder.Companion.ONITEM_LONG_CLICK
import com.quick.liveswitcher.ui.bottomfragment.picture.PictureRcvItemData
import com.quick.liveswitcher.ui.dialog.DeleteTipDialog
import com.quick.liveswitcher.ui.dialog.LayerDeleteTipDialog
import com.quick.liveswitcher.utils.ToastUtils

class AppFragment : Fragment(), OnItemClickListener {
    private lateinit var binding: FragmentAppBinding
    private lateinit var mAppAdapter: AppAdapter
    private var mAppSelectDialog: AppSelectDialog? = null
    private var mDeleteTipDialog: DeleteTipDialog? = null
    private var mAppDataList: ArrayList<AppBean> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAppBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        regEvent()
    }

    override fun onResume() {
        super.onResume()
        assembleData()
    }

    private fun initView() {
        mAppAdapter = AppAdapter(context, this)
        binding.appRcv.layoutManager = GridLayoutManager(context, 5)
        binding.appRcv.addItemDecoration(AppRcvItemSpacesDecoration())
        binding.appRcv.adapter = mAppAdapter
    }

    private fun assembleData() {
        val appRcvItemDataList = ArrayList<AppBean>()
        val addBean = AppBean()
        addBean.appName = "add"
        addBean.appIconName = "app_icon_plus"
        appRcvItemDataList.add(addBean)

        val showAppList = findMatchingPackages()

        SceneDataManager.getCurrentScene().appList.forEach {
            it.isSelected = showAppList.contains(it)
            appRcvItemDataList.add(it)
        }

        mAppAdapter.showData(appRcvItemDataList)
        mAppDataList = appRcvItemDataList

    }

    private fun findMatchingPackages(): List<AppBean> {
        val matchingPackages = mutableListOf<AppBean>()
        for (app in SceneDataManager.getCurrentScene().appList) {
            for (layer in SceneDataManager.getCurrentScene().layerList) {
                if (app.appPackageName == layer.appPackageName) {
                    matchingPackages.add(app)
                }
            }
        }
        return matchingPackages
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (SceneDataManager.isScanMode) {
            ToastUtils.show(R.string.current_is_scan)
            return
        }
        val data = mAppAdapter.getItem(position)
        if (id == ONITEM_LONG_CLICK) {
            val tip = "确定要删除${data?.appName ?: ""}吗？"
            showLayerDeleteTipDialog(tip, data)
            return
        }
        if (position == 0) {
            showAppSelectDialog()
        } else {
            val itemBean = mAppDataList.get(position)
            if (itemBean.isSelected) {
                val layerId = SceneDataManager.getLayerIdByPackageName(itemBean.appPackageName)
                if (layerId != null) {
                    LayerController.removeLayer(layerId)
                }
            } else {
                LayerController.addAppLayer(SceneDataManager.getDefaultAppLayerBean(itemBean))
            }
        }
    }


    fun regEvent() {
        Log.d("tag", "regEvent")
        //有图层被移除了
        LiveDataBus.withMulti(EventAction.ON_LAYER_REMOVE, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            assembleData()
        })
        //添加了新图层
        LiveDataBus.withMulti(EventAction.ON_LAYER_ADD, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            assembleData()
        })
        //删除了app
        LiveDataBus.withMulti(EventAction.ON_APP_DELETED, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            assembleData()
        })

    }


    fun showAppSelectDialog() {
        if (mAppSelectDialog == null) {
            mAppSelectDialog = AppSelectDialog(requireActivity())
            mAppSelectDialog?.mOnAppSelectListener = object : OnAppSelectListener {
                override fun OnAppSelect(data: AppBean?) {
                    if (data != null) {
                        SceneDataManager.addAppToBtmList(data)
                        assembleData()
                        SceneDataManager.updateDbScene(SceneDataManager.getCurrentScene())
                    }
                    mAppSelectDialog?.dismiss()
                }
            }
        }
        mAppSelectDialog?.show()
    }

    fun showLayerDeleteTipDialog(tip: String?, appData: AppBean?) {
        mDeleteTipDialog = DeleteTipDialog(requireActivity())
        mDeleteTipDialog?.mOnDialogOkCancelClickListener = object :
            OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                SceneDataManager.deleteApp(appData)
                SceneDataManager.updateDbScene(SceneDataManager.getCurrentScene())
            }

            override fun OnDialogCancelClick() {

            }

        }
        mDeleteTipDialog?.tip = tip
        mDeleteTipDialog?.show()
    }
}