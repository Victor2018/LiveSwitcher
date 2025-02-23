package com.quick.liveswitcher.ui.bottomfragment.scene

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.quick.liveswitcher.action.EventAction
import com.quick.liveswitcher.data.scene.AppBean
import com.quick.liveswitcher.data.scene.LayerBean
import com.quick.liveswitcher.data.scene.SceneBean
import com.quick.liveswitcher.databinding.FragmentScenceBinding
import com.quick.liveswitcher.db.DBInjector
import com.quick.liveswitcher.db.vm.SceneVM
import com.quick.liveswitcher.interfaces.OnDialogOkCancelClickListener
import com.quick.liveswitcher.livedatabus.LiveDataBus
import com.quick.liveswitcher.ui.dialog.DeleteTipDialog
import com.quick.liveswitcher.ui.previewarea.LayerController
import com.quick.liveswitcher.utils.JsonUtils
import com.quick.liveswitcher.utils.ToastUtils
import okhttp3.internal.notify


class SceneFragment : Fragment() {
    private lateinit var binding: FragmentScenceBinding
    private lateinit var mAdapter: SceneRcvAdapter
    private var mDeleteTipDialog: DeleteTipDialog? = null

    private val sceneVM: SceneVM by viewModels {
        DBInjector.provideSceneVMFactory(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentScenceBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeUi()
        initData()
        regEvent()
    }

    override fun onResume() {
        super.onResume()
        Log.d("tag", "scene fragment onResume")
        assembleData()
    }

    private fun initData() {
        mAdapter = SceneRcvAdapter()
        binding.sceneRcv.layoutManager = GridLayoutManager(context, 5)
        binding.sceneRcv.addItemDecoration(SceneRcvItemSpacesDecoration())
        binding.sceneRcv.adapter = mAdapter
        binding.sceneRcv.itemAnimator = DefaultItemAnimator().apply {
            supportsChangeAnimations = false
            changeDuration = 0
        }

        mAdapter.setOnItemClickListener {
            if (it.sceneName == "scan") {
                //扫描
                SceneDataManager.switchScene(SceneDataManager.getScanModeSceneBean(), true)


            } else if (it.sceneName == "add") {
                SceneDataManager.addScene()
            } else {
                if (LayerController.getSceneSelectedJob() == null || LayerController.getSceneSelectedJob()?.isCompleted == true) {
                    if (it.isSelected) {
                        //如果当前是被选中状态，则不执行切换场景
                    } else {
                        SceneDataManager.switchScene(it)
                    }
                } else {
                    ToastUtils.show("场景切换过快")
                }
            }
        }
        mAdapter.setOnLongClickListener {
            if (it.sceneName == "scan") {
                //扫描

            } else if (it.sceneName == "add") {

            } else {
                showLayerDeleteTipDialog(it.sceneName, it)
            }
        }
    }


    fun showLayerDeleteTipDialog(tip: String?, sceneBean: SceneBean) {
        mDeleteTipDialog = DeleteTipDialog(requireActivity())
        mDeleteTipDialog?.mOnDialogOkCancelClickListener = object :
            OnDialogOkCancelClickListener {
            override fun OnDialogOkClick() {
                SceneDataManager.deleteScene(sceneBean)
            }

            override fun OnDialogCancelClick() {

            }

        }
        mDeleteTipDialog?.tip = tip
        mDeleteTipDialog?.show()
    }

    // 注册事件
    @SuppressLint("NotifyDataSetChanged")
    private fun regEvent() {
        LiveDataBus.withMulti(EventAction.ON_SCENE_SELECTING, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            assembleData()
        })
        LiveDataBus.withMulti(EventAction.ON_SCENE_DELETED, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            assembleData()
        })
        //截图更新了
        LiveDataBus.withMulti(EventAction.SCENE_SHOT_UPDATE, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            if (it is SceneBean) {
                updateBg(it)
            }
        })
    }


    private fun updateBg(sceneBean: SceneBean) {
        Log.d("tag", "updateBg:${sceneBean.sceneName}")
        val dataList = mAdapter.getDataList()
        for ((index, item) in dataList.withIndex()) {
            if (item == sceneBean) {
                mAdapter.notifyItemChanged(index, "updateBg")
            }
        }

    }

    private fun assembleData() {
        val sceneRcvItemDataList = ArrayList<SceneBean>()
        val scanBean = SceneDataManager.getScanModeSceneBean()
        sceneRcvItemDataList.add(scanBean)

        val plusBean = SceneBean()
        plusBean.sceneName = "add"
        plusBean.appIconName = "scene_icon_plus"
        sceneRcvItemDataList.add(plusBean)

        sceneRcvItemDataList.addAll(SceneDataManager.getAllSceneList())
        mAdapter.setData(sceneRcvItemDataList)
    }


    fun subscribeUi() {
        sceneVM.allSceneData.observe(viewLifecycleOwner, Observer {

        })
    }


    fun addScene() {
        val layerList = ArrayList<LayerBean>()
        for (i in 0..2) {
            val bean = LayerBean()
            bean.name = "111"
            layerList.add(bean)
        }
        val appList = ArrayList<AppBean>()
        for (i in 0..2) {
            val bean = AppBean()
            bean.appName = "222"
            appList.add(bean)
        }
        val data = SceneBean(
            "测试", false,
            "/sdcard/test.png", "/sdcard/testiv.png",
            JsonUtils.toJSONString(layerList), JsonUtils.toJSONString(appList)
        )
        sceneVM.insert(data)
    }

    fun addSceneToDb(sceneBean: SceneBean) {
        sceneVM.insert(sceneBean)
    }

    fun deleteScene() {
        sceneVM.delete(0)
    }

    fun updateLayerList(layerListStr: String, id: String) {
        sceneVM.updateLayerList(layerListStr, id)
    }

    fun updateAppList(appListStr: String, id: String) {
        sceneVM.updateAppList(appListStr, id)
    }


}