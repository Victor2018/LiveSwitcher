package com.quick.liveswitcher.ui.bottomfragment.camera

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.cherry.permissions.lib.EasyPermissions
import com.cherry.permissions.lib.annotations.AfterPermissionGranted
import com.quick.liveswitcher.R
import com.quick.liveswitcher.databinding.FragmentCameraBinding
import com.cherry.permissions.lib.dialogs.SettingsDialog
import com.google.android.material.snackbar.Snackbar
import com.quick.liveswitcher.action.EventAction
import com.quick.liveswitcher.ui.bottomfragment.scene.SceneDataManager.getDefaultCameraLayerBean
import com.quick.liveswitcher.ui.previewarea.LayerController
import com.quick.liveswitcher.livedatabus.LiveDataBus
import com.quick.liveswitcher.ui.bottomfragment.scene.SceneDataManager
import com.quick.liveswitcher.utils.ToastUtils

class CameraFragment : Fragment(), OnItemClickListener, EasyPermissions.PermissionCallbacks {

    companion object {
        const val REQUEST_CODE_CAMERA_PERMISSION = 123
    }

    private lateinit var binding: FragmentCameraBinding

    private lateinit var mAdapter: CameraRcvAdapter

    private var currentCameraId: Int? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCameraBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        regEvent()
    }


    override fun onResume() {
        super.onResume()
        assembleData()
    }


    fun regEvent() {
        Log.d("tag","regEvent" )
        //有图层被移除了
        LiveDataBus.withMulti(EventAction.ON_LAYER_REMOVE, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            Log.d("tag","ON_LAYER_REMOVE:$it   currentCameraId:$currentCameraId" )
            if (it is Int) {
                if (currentCameraId == it) { //当前移除的是相机图层才处理数据
                    assembleData()
                }
            }
        })

        //添加了新图层
        LiveDataBus.withMulti(EventAction.ON_LAYER_ADD, javaClass.simpleName).observe(viewLifecycleOwner, Observer {
            val cameraLayer = LayerController.getCameraLayer()
            if (cameraLayer != null) {
                assembleData()
            }
        })
    }


    private fun initData() {
        mAdapter = CameraRcvAdapter()
        mAdapter.mOnItemClickListener = this
        binding.cameraRcv.layoutManager = GridLayoutManager(context, 5)
        binding.cameraRcv.addItemDecoration(CameraRcvItemSpacesDecoration())
        binding.cameraRcv.adapter = mAdapter
    }

    private fun assembleData() {
        val cameraLayer = LayerController.getCameraLayer()
        currentCameraId = cameraLayer?.layerId
        val cameraRcvItemDataList = ArrayList<CameraRcvItemData>()
        var cameraRcvItemData = CameraRcvItemData()
        cameraRcvItemData.name = "camera"
        cameraRcvItemData.icon = R.mipmap.camera_icon
        cameraRcvItemData.cameraLayer = cameraLayer
        cameraRcvItemDataList.add(cameraRcvItemData)
        mAdapter.setData(cameraRcvItemDataList)
    }


    override fun onItemClick(p0: AdapterView<*>?, v: View?, position: Int, p3: Long) {
        val cameraLayer = LayerController.getCameraLayer()

            if (SceneDataManager.isScanMode) {
                ToastUtils.show(R.string.current_is_scan)
                return
            }


        if (cameraLayer != null) { //当前显示摄像头图层 点击后移除摄像头图层
            LayerController.removeLayer(LayerController.getCameraLayer())
        } else {
            val cameraLayerBean = getDefaultCameraLayerBean()
            LayerController.addCameraLayer(cameraLayerBean)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    // ============================================================================================
    //  Implementation Permission Callbacks
    // ============================================================================================

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        //会回调 AfterPermissionGranted注解对应方法
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        val settingsDialogBuilder = SettingsDialog.Builder(requireContext())

        when (requestCode) {
            REQUEST_CODE_CAMERA_PERMISSION -> {
                settingsDialogBuilder.title = getString(
                    com.cherry.permissions.lib.R.string.title_settings_dialog,
                    "Camera Permission"
                )
                settingsDialogBuilder.rationale = getString(
                    com.cherry.permissions.lib.R.string.rationale_ask_again,
                    "Camera Permission"
                )
            }
        }

        settingsDialogBuilder.build().show()
    }

    // ============================================================================================
    //  Private Methods
    // ============================================================================================

    @AfterPermissionGranted(REQUEST_CODE_CAMERA_PERMISSION)
    private fun requestCameraPermission() {
        if (EasyPermissions.hasPermissions(context, Manifest.permission.CAMERA)) {
            // Have permission, do the thing!
            showMessage(binding.root, "AfterPermissionGranted you have Camera permission")
        } else {
            // Request one permission
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.permission_camera_rationale_message),
                REQUEST_CODE_CAMERA_PERMISSION,
                Manifest.permission.CAMERA
            )
        }
    }

    fun showMessage(view: View, message: String) {
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }


}