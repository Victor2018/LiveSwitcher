package com.quick.liveswitcher.ui.operatearea

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.quick.liveswitcher.R
import com.quick.liveswitcher.action.EventAction
import com.quick.liveswitcher.data.LayerOperateItemBean
import com.quick.liveswitcher.databinding.FragmentOperaAreaVideoBinding
import com.quick.liveswitcher.ui.previewarea.LayerController
import com.quick.liveswitcher.utils.ResUtils
import com.quick.liveswitcher.utils.ViewUtils.hide
import com.quick.liveswitcher.utils.ViewUtils.show
import com.quick.liveswitcher.livedatabus.LiveDataBus
import com.quick.liveswitcher.ui.bottomfragment.scene.SceneDataManager
import com.quick.liveswitcher.utils.ToastUtils

class VideoOperaFragment : Fragment(), OnItemClickListener, OnClickListener {
    companion object {

        fun newInstance(): VideoOperaFragment {
            val fragment = VideoOperaFragment()
            return fragment
        }
    }

    lateinit var binding: FragmentOperaAreaVideoBinding

    lateinit var mLayerAdapter: LayerAdapter
    lateinit var mItemTouchHelperCallback: ItemTouchHelperCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOperaAreaVideoBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun initView() {
        subscribeEvent()

        mLayerAdapter = LayerAdapter(context, this)
        binding.mRvLayer.adapter = mLayerAdapter
        mItemTouchHelperCallback = ItemTouchHelperCallback(mLayerAdapter)
        val mItemTouchHelper = ItemTouchHelper(mItemTouchHelperCallback)
        mItemTouchHelper.attachToRecyclerView(binding.mRvLayer)
        initEvent()
    }

    fun initEvent() {
        mItemTouchHelperCallback.setOnMoveCallback { fromPosition, toPosition ->
            // 注意：反序的集合
            var oriFromPosition = mLayerAdapter.mDatas.size - 1 - fromPosition
            var oriToPosition = mLayerAdapter.mDatas.size - 1 - toPosition
            LayerController.setSelectedSceneLayerOrder(oriFromPosition, oriToPosition)
        }
        binding.mVideoOperaMenu.setDeleteIconClickListener(object : OnClickListener {
            override fun onClick(v: View?) {
                LayerController.getCurrentSelectedLayerId()?.let { LayerController.removeLayer(it) }
            }

        })
        binding.mTvClearLayer.setOnClickListener(this)
//        mLayerAdapter.listener  = object : LayerAdapter.OnItemClickListener {
//        }
    }

    fun initData() {
        mLayerAdapter.clear()


        mLayerAdapter.setDatas(getLayerOperateItemList())
        mLayerAdapter.notifyDataSetChanged()
    }

    fun getLayerOperateItemList(): ArrayList<LayerOperateItemBean> {
        val layerOperateItemBeanList: ArrayList<LayerOperateItemBean> = ArrayList()
        for (layer in LayerController.getCurrentSceneBean().layerList.reversed()) {
            layer.layerId
            val layerOperateItemBean: LayerOperateItemBean = LayerOperateItemBean()
            layerOperateItemBean.layerId = layer.layerId
            layerOperateItemBean.layerType = layer.layerType
            layerOperateItemBean.name = layer.name ?: ""
            when (layer.layerType) {
                1 -> {
                    layerOperateItemBean.imgPath = layer.materialPath
                }

                2, 3, 4, 5 -> {
                    layerOperateItemBean.drawableName = layer.appIconName
                }
            }
            layerOperateItemBeanList.add(layerOperateItemBean)
        }
        return layerOperateItemBeanList
    }


    fun setHasLayerChecked(hasLayerChecked: Boolean) {
        mLayerAdapter.hasLayerChecked = hasLayerChecked
        mLayerAdapter.notifyDataSetChanged()
        setRvLayoutParm(hasLayerChecked)
    }

    fun setRvLayoutParm(hasLayerChecked: Boolean) {
        val layoutParams = binding.mClLayer.layoutParams
        if (hasLayerChecked) {
            binding.mVideoOperaMenu.show()
            binding.mTvClearLayer.hide()
            layoutParams.width = ResUtils.getDimenPixRes(R.dimen.dp_90)
        } else {
            binding.mVideoOperaMenu.hide()
            binding.mTvClearLayer.show()
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
        }
        binding.mClLayer.layoutParams = layoutParams
    }

    fun subscribeEvent() {
        LiveDataBus.withMulti(EventAction.TAB_CHANGED, javaClass.simpleName).observe(viewLifecycleOwner,
            Observer {
                if (it is Int) {
//                binding.mVideoOperaMenu.setOperaMenuByTab(it)
                }
            })

        //当前有图层被选中
        LiveDataBus.withMulti(EventAction.ON_LAYER_SELECTED, javaClass.simpleName).observe(viewLifecycleOwner,
            Observer {
                LayerController.getCurrentSelectedLayerId()?.let {
                    setHasLayerChecked(true)
                    binding.mVideoOperaMenu.onSelectedLayer(it)
                }
            }
        )

        //当没有图层被选中
        LiveDataBus.withMulti(EventAction.ON_LAYER_UNSELECTED, javaClass.simpleName).observe(viewLifecycleOwner,
            Observer {
                setHasLayerChecked(false)
            }
        )

        //有图层被移除了
        LiveDataBus.withMulti(EventAction.ON_LAYER_REMOVE, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            initData()
        })

        //选择了新的场景
        LiveDataBus.withMulti(EventAction.ON_SCENE_SELECTING, javaClass.simpleName).observe(viewLifecycleOwner,
            Observer {
                initData()
            }
        )
        //添加了新图层
        LiveDataBus.withMulti(EventAction.ON_LAYER_ADD, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            initData()
        })
        //图层层级变化
        LiveDataBus.withMulti(EventAction.ON_LAYER_HIERARCHY_MOVE, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            initData()
        })

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val data = mLayerAdapter.getItem(position)
        if (mLayerAdapter.hasLayerChecked) { //app被选择的时候，点击右边的图层icon 切换图层
            Log.d("tag", "data:${data?.name}")
            data?.layerId?.let { LayerController.onLayerSelected(it) }
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.mTvClearLayer -> {
                if (SceneDataManager.isScanMode) {
                    ToastUtils.show(R.string.current_is_scan)
                    return
                }
                LayerController.removeAllLayers()
            }
        }
    }

}