package com.quick.liveswitcher.ui.previewarea

import android.content.Context
import android.graphics.Color
import android.graphics.RectF
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global
import android.util.Log
import android.util.Size
import android.view.Surface
import com.quick.liveswitcher.BaseApp
import com.quick.liveswitcher.action.EventAction
import com.quick.liveswitcher.data.scene.LayerBean
import com.quick.liveswitcher.data.scene.SceneBean
import com.quick.liveswitcher.livedatabus.LiveDataBus
import com.quick.liveswitcher.ui.bottomfragment.scene.SceneDataManager
import com.tripod.daobotai.virtualscreen.VirtualScreenHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import sdk.smartx.director.SXSLayerApi
import sdk.smartx.director.layer.CameraLayer
import sdk.smartx.director.layer.IRenderLayer
import sdk.smartx.director.layer.KeyCutType
import sdk.smartx.director.layer.LayerConfig.ConfigBuilder
import sdk.smartx.director.layer.LayerType
import sdk.smartx.director.layer.MarqueeTextLayer
import sdk.smartx.director.layer.Material
import sdk.smartx.director.layer.PictureLayer
import sdk.smartx.director.layer.ScaleType
import sdk.smartx.director.layer.SurfaceLayer
import sdk.smartx.director.layer.TextGravityType
import sdk.smartx.director.layer.VideoLayer
import java.util.Collections


object LayerController {
    private var mPreviewAreaFragment: PreviewAreaFragment? = null
    var mLayerApi: SXSLayerApi? = null
    private val mPreviewRatio = 9.0f / 16.0f
    private var currentSelectedLayerId: Int? = null
    private var currentSelectedLayer: IRenderLayer? = null
    private var mCurrentSelectedLayerBean: LayerBean? = null
    private var mCurrentSceneBean = SceneBean()
    private var mSceneList = ArrayList<SceneBean>()
    private var mPhysicsSurfaceWith = 0
    private var mPhysicsSurfaceHeight = 0
    private var mSceneSelectedJob: kotlinx.coroutines.Job? = null
    private var mHandler = Handler(Looper.getMainLooper())

    fun init(fragment: PreviewAreaFragment, layerApi: SXSLayerApi?, physicsSurfaceWith: Int, physicsSurfaceHeight: Int) {
        mPreviewAreaFragment = fragment
        mLayerApi = layerApi
        mPhysicsSurfaceWith = physicsSurfaceWith
        mPhysicsSurfaceHeight = physicsSurfaceHeight
    }

    fun onSceneSelected(sceneBean: SceneBean) {
        mCurrentSceneBean = sceneBean
        postSceneSelectingEvent()

        if (SceneDataManager.isScanMode) {
            mPreviewAreaFragment?.setEditEnable(false)
        } else {
            mPreviewAreaFragment?.setEditEnable(true)
        }

        mSceneSelectedJob = GlobalScope.launch(context = Dispatchers.IO) {
            removeAllLayers(isSwitchScene = true)
            mCurrentSceneBean.layerList.forEach {
                showDefaultLayer(it)
            }
            postSceneSelectedEvent()
        }

    }

    fun getSceneSelectedJob(): kotlinx.coroutines.Job? {
        return mSceneSelectedJob
    }

    fun showDefaultLayer(layerBean: LayerBean) {
        when (layerBean.layerType) { //1: 图片，2：相机，3：视频，4：文字，5：APP
            1 -> { //图片

            }

            2 -> { //相机
                addCameraLayer(layerBean, true)
            }

            3 -> { //视频

            }

            4 -> { //文字

            }

            5 -> { //APP
                addAppLayer(layerBean, true)
            }
        }
    }

    //添加场景
    fun addScene() {
        var sceneBean = SceneBean()
    }

    // 添加背景图层
    fun addPicLayer(picPath: String, picName: String = "picture", isBackground: Boolean = false) {
        //添加背景图层
        var layerBean = LayerBean()
        layerBean.layerType = 1
        layerBean.name = picName
        if (isBackground) {
            layerBean.rect = RectF(0.0f, 0.0f, 1.0f, 1.0f)
        } else {
            layerBean.rect = RectF(0.4f, 0.4f, 1.0f, 1.0f)
        }
        layerBean.alpha = 1f
        layerBean.materialPath = picPath
        layerBean.scaleType = 2
        layerBean.visible = 1

        val bgConfig = ConfigBuilder(
            changeLayerTypeToEnum(layerBean.layerType), layerBean.name, layerBean.visible, layerBean.rect
        )
            .setMaterial(Material(layerBean.name, 0L, layerBean.materialPath))
            .setAlpha(layerBean.alpha).setScaleType(changeScaleType(layerBean.scaleType))
            .setRotate(layerBean.rotate)
            .build()
        var pictureLayer = PictureLayer(bgConfig)
        layerBean.layerId = pictureLayer.layerId

        if (isBackground) {
            mLayerApi?.addLayer(0, pictureLayer)
            mCurrentSceneBean.layerList.add(0, layerBean)
        } else {
            mLayerApi?.addLayer(pictureLayer)
            mCurrentSceneBean.layerList.add(layerBean)
        }
    }

    fun addCameraLayer(layerBean: LayerBean, isDefault: Boolean = false) {
        val currentT1 = System.currentTimeMillis()
        val cameraConfig = ConfigBuilder(
            changeLayerTypeToEnum(layerBean.layerType), layerBean.name, layerBean.visible, layerBean.rect
        )
            .setVirtualBackground(layerBean.virtualBackground == 1)
            .setGreenSource(layerBean.greenSource == 1)
            .setHoriMirror(layerBean.hormirror == 1)
            .setGreenSourceType(KeyCutType.Green)
            .setCutType(changeCutType(layerBean.cutType))
            .setBeauty(layerBean.isbeauty == 1)
            .setCutProtectLevel(layerBean.cutprotectlevel)
            .setScaleType(changeScaleType(layerBean.scaleType))
            .setCutLevel(layerBean.cutLevel)
            .setMaterial(Material(layerBean.name, 0, "")) //置空表示不设置背景图片，单纯抠图，抠成透明//
            .setRotate(layerBean.rotate)
            .build()
        val cameraLayer = CameraLayer(getCameraId(), Size(1080, (1080 / mPreviewRatio).toInt()), cameraConfig)
        mLayerApi?.addLayer(cameraLayer)
        layerBean.layerId = cameraLayer.layerId

        if (!isDefault) { //如果不是回显 则添加对象
            mCurrentSceneBean.layerList.add(layerBean)
        }
        postAddLayerEvent()
        postLayerUnSelectedEvent()
        updateLocalSceneBean()

    }

    fun removeLayer(layerId: Int?) {
        layerId?.let {
            var layer = getLayerById(it)
            if (layer != null) {
                checkCurrentLayerIsRemoveLayer(it)
                mLayerApi?.removeLayer(it)
                mCurrentSceneBean.layerList.removeAll { it2 -> it2.layerId == layerId }
                postRemoveLayerEvent(it)
                updateLocalSceneBean()
            }
        }
    }

    fun removeLayer(layer: IRenderLayer?, isSwitchScene: Boolean = false) {
        layer?.let {
            val layerId = it.layerId
            checkCurrentLayerIsRemoveLayer(layerId)
            mLayerApi?.removeLayer(layer)
            if (!isSwitchScene) { //如果是切换场景，不删除数据。
                mCurrentSceneBean.layerList.removeAll { it2 -> it2.layerId == layerId }
            }
            postRemoveLayerEvent(layerId)
            updateLocalSceneBean()
        }
    }

    // 移除所有图层
    fun removeAllLayers(isSwitchScene: Boolean = false) {
        val layerNum = mLayerApi?.layers?.size ?: 0
        if (layerNum > 0) {
            val layer = mLayerApi?.layers?.get(0)
            if (layer?.layerId != null) {
                removeLayer(layer, isSwitchScene)
            }
            val layerNum2 = mLayerApi?.layers?.size ?: 0
            if (layerNum2 > 0) {
                removeAllLayers(isSwitchScene)
            }
        }
    }


    fun checkCurrentLayerIsRemoveLayer(layerId: Int) {
        if (currentSelectedLayerId == layerId) {
            onLayerUnSelected()
        }
    }

    //获取当前场景中的相机图层
    fun getCameraLayer(): IRenderLayer? {
        var cameraLayer: IRenderLayer? = null
        mLayerApi?.layers?.forEach {
            if (it is CameraLayer) {
                cameraLayer = it
            }
        }
        return cameraLayer
    }


    // 添加视频图层
    fun addVideoLayer(videoPath: String, videoName: String = "video") {
        val r = RectF(0.0f, 0.0f, 1.0f, 0.3f)
        val videoConfig = ConfigBuilder(LayerType.Media, videoName, 1, r)
            .setMaterial(Material(videoName, 0L, videoPath)).setVolume(0.8f)
            .setGreenSource(false)
            .setCutLevel(0.5f)
            .setGreenSourceType(KeyCutType.Close).build()
        val videoLayer = VideoLayer(videoConfig, true)
        mLayerApi?.addLayer(videoLayer)
    }

    //添加跑马灯图层
    fun addMarqueeTextLayer(text: String) {
        val txtConfig = ConfigBuilder(
            LayerType.Text,
            "marqueeText",
            1,
            RectF(0.5f, 0.5f, 1.0f, 1f)
        ).setTextContent("Pixsmart图层处理SDK演示跑马灯文字").setTextColor(
            Color.valueOf(Color.RED)
        ) //.setTextBgColor(Color.valueOf(Color.WHITE))
            .setTextSize(10.0f) //dp
            .setTextSpeed(1f) //[0-30]
            .setTextGravity(TextGravityType.Center) //.setHoriMirror(true)
            //.setRotate(270)
            .build()
        val tLayer = MarqueeTextLayer(Size(500, 200), txtConfig)
        mLayerApi!!.addLayer(tLayer)
    }


    //添加app图层
    fun addAppLayer(layerBean: LayerBean, isDefault: Boolean = false) {
        //物理surface的尺寸
        val physicsSurfaceWith = mPhysicsSurfaceWith
        val physicsSurfaceHeight = mPhysicsSurfaceHeight
        //虚拟surface尺寸
        val virtualSurfaceWith = physicsSurfaceWith * 2
        val virtualSurfaceHeight = physicsSurfaceHeight * 2

        //添加surface 图层
        val surfaceConfig = ConfigBuilder(
            changeLayerTypeToEnum(layerBean.layerType), layerBean.name, layerBean.visible, layerBean.rect
        ).setVirtualBackground(layerBean.virtualBackground == 1)
            .setGreenSource(layerBean.greenSource == 1)
            .setGreenSourceType(KeyCutType.Green)
            .setCutType(changeCutType(layerBean.cutType))
            .setBeauty(layerBean.isbeauty == 1)
            .setCutProtectLevel(layerBean.cutprotectlevel)
            .setScaleType(changeScaleType(layerBean.scaleType))
            .setCutLevel(layerBean.cutLevel)
            .build()
        val surfaceLayer = SurfaceLayer(
            Size(virtualSurfaceWith, virtualSurfaceHeight),
            object : SurfaceLayer.OnSurfaceEventListener {
                override fun onSurfaceTextureCreated(p0: SurfaceTexture?, surface: Surface?): Boolean {
                    Log.e("TAG", "onSurfaceTextureCreated")
                    mHandler.post {
                        VirtualScreenHelper.instance.addVirtualSeat(
                            layerBean.appPackageName,
                            surface!!,
                            Size(virtualSurfaceWith, virtualSurfaceHeight),
                            Size(physicsSurfaceWith, physicsSurfaceHeight)
                        )
                    }
                    return true
                }

                override fun onSurfaceLayerRelease() {

                }

            },
            surfaceConfig
        )
        mLayerApi!!.addLayer(surfaceLayer)
        layerBean.layerId = surfaceLayer.layerId

        if (!isDefault) { //如果不是回显 则添加对象
            mCurrentSceneBean.layerList.add(layerBean)
        }

        postAddLayerEvent()
        postLayerUnSelectedEvent()
        updateLocalSceneBean()
    }


    //画板中当前选中的图层
    fun onLayerSelected(layerId: Int) {
        Log.d("tag", "onLayerSelected:$layerId")
        currentSelectedLayerId = layerId
        currentSelectedLayer = getLayerById(layerId)
        mCurrentSceneBean.layerList.forEach {
            if (it.layerId == layerId) {
                mCurrentSelectedLayerBean = it
            }
        }
        postLayerSelectedEvent()
    }

    //当前未选中任何图层
    fun onLayerUnSelected() {
        currentSelectedLayer = null
        currentSelectedLayerId = null
        mCurrentSelectedLayerBean = null
        postLayerUnSelectedEvent()
    }


    // 通过图层id获取图层对象
    fun getLayerById(layerId: Int): IRenderLayer? {
        val list = mLayerApi!!.layers
        for (i in list.indices) {
            if (layerId == list[i].layerId) {
                return list[i]
            }
        }
        return null
    }

    fun getCurrentSelectedLayerId(): Int? {
        return currentSelectedLayerId
    }

    fun getCurrentSceneBean(): SceneBean {
        return mCurrentSceneBean
    }

    // 按图层降序排序
    fun getOrderedLayerListByDescending(): List<IRenderLayer> {
        val list = mLayerApi!!.layers
        val sortedList = list.sortedByDescending { mLayerApi!!.getLayerOrder(it.layerId) }
        return sortedList
    }

    //新场景开始切换
    fun postSceneSelectingEvent() {
        SceneDataManager.updateDbSceneSelectedStatus(mCurrentSceneBean.sceneName)
        LiveDataBus.sendMulti(EventAction.ON_SCENE_SELECTING)
    }

    //新的场景被选中
    fun postSceneSelectedEvent() {
        LiveDataBus.sendMulti(EventAction.ON_SCENE_SELECTED)
    }

    //发送图层未选中的消息
    fun postLayerUnSelectedEvent() {
        mPreviewAreaFragment?.currentSelectedLayerRemove()// 通知移除当前图层选中框
        LiveDataBus.sendMulti(EventAction.ON_LAYER_UNSELECTED)
    }

    //发送图层被选中的消息
    fun postLayerSelectedEvent() {
        LiveDataBus.sendMulti(EventAction.ON_LAYER_SELECTED)
    }

    fun postAddLayerEvent() {
        LiveDataBus.sendMulti(EventAction.ON_LAYER_ADD)
    }

    //发送图层移除的消息
    fun postRemoveLayerEvent(layerId: Int) {
        LiveDataBus.sendMulti(EventAction.ON_LAYER_REMOVE, layerId)
    }

    //层级移动
    fun postLayerHierarchyMoveEvent() {
        LiveDataBus.sendMulti(EventAction.ON_LAYER_HIERARCHY_MOVE)
    }

    fun getSceneBeanLayerPosition(currentSelectedLayerId: Int): Int? {
        var position: Int? = null
        for ((index, bean) in mCurrentSceneBean.layerList.withIndex()) {
            if (bean.layerId == currentSelectedLayerId) {
                position = index
                break
            }
        }
        return position
    }

    //更新本地bean
    fun updateLocalSceneBean() {
        SceneDataManager.updateDbScene(mCurrentSceneBean)
    }


    // ---------当前选中图层的设置与信息获取 start ------------

    fun notifySelectedLayerBeanRectChange() {
        mCurrentSelectedLayerBean?.rect = currentSelectedLayer?.rect!!
    }

    //图层上一层
    fun setSelectedForwardLayer() {
        currentSelectedLayerId?.let {
            var index: Int = mLayerApi!!.getLayerOrder(it)
            val order = mLayerApi!!.orderList
            if (index != -1) {
                index += 1
                if (index >= order.size) return  //已经在最前面了//
                mLayerApi?.setLayerOrder(index, it)
                // 注意 index 已经加1了
                Collections.swap(mCurrentSceneBean.layerList, index, index - 1)
            }
            postLayerHierarchyMoveEvent()
            updateLocalSceneBean()
        }
    }

    //图层下一层
    fun setSelectedBackLayer() {
        currentSelectedLayerId?.let {
            var index: Int = mLayerApi!!.getLayerOrder(it)
            if (index != -1) {
                index -= 1
                if (index < 0) return  //已经在最前面了
                mLayerApi!!.setLayerOrder(index, it)
                //注意 index 已经减1了
                Collections.swap(mCurrentSceneBean.layerList, index, index + 1)
            }
            postLayerHierarchyMoveEvent()
            updateLocalSceneBean()
        }

    }

    //图层置顶
    fun setSelectedToTopLayer() {
        currentSelectedLayerId?.let {
            val currentIndex: Int = mLayerApi!!.getLayerOrder(it)
            var index = mLayerApi!!.orderList?.size ?: 0
            index -= 1
            if (currentIndex == index) {
                //当移到底部了再改变sceneBean里面集合的顺序
                var position = getSceneBeanLayerPosition(it)
                if (position != null) {
                    for (i in position until (mCurrentSceneBean.layerList.size - 1)) {
                        Collections.swap(mCurrentSceneBean.layerList, i, i + 1)
                    }
                }
                postLayerHierarchyMoveEvent()
                updateLocalSceneBean()
                return
            }
            if (index < 0) {
                return
            }
            setSelectedForwardLayer()
            val afterIndex: Int = mLayerApi!!.getLayerOrder(it)
            if (afterIndex < index) {
                setSelectedToTopLayer()
            }
            //不能通过直接setLayerOrder来置顶，如果一次调整多个层级有bug
//        Log.d("tag","result:$index")
//        mLayerApi!!.setLayerOrder(index, currentSelectedLayerId)
        }
    }

    //图层置底
    fun setSelectedToBottomLayer() {
        currentSelectedLayerId?.let {
            var index: Int = mLayerApi!!.getLayerOrder(it)
            if (index <= 0) {
                //当移到顶部了再改变sceneBean里面集合的顺序
                var position = getSceneBeanLayerPosition(it)
                if (position != null) {
                    for (i in position downTo 1) {
                        Collections.swap(mCurrentSceneBean.layerList, i, i - 1)
                    }
                }
                postLayerHierarchyMoveEvent()
                updateLocalSceneBean()
                return
            }
            setSelectedBackLayer()
            val afterIndex: Int = mLayerApi!!.getLayerOrder(it)
            if (afterIndex > 0) {
                setSelectedToBottomLayer()
            }
            //不能通过直接setLayerOrder来置顶，如果一次调整多个层级有bug
//        mLayerApi!!.setLayerOrder(0, currentSelectedLayerId)
        }
    }


    //当前选中场景的图层顺序
    fun setSelectedSceneLayerOrder(fromPosition: Int, toPosition: Int) {
        val touchLayerId = mCurrentSceneBean.layerList[fromPosition].layerId
        val result = mLayerApi?.setLayerOrder(toPosition, touchLayerId)
        Log.d("tag", "result：${result}")
        if (result != -1 && result != -2) { //-1:当前图层还没有add, -2:index非法
            if (fromPosition < toPosition) {
                for (i in fromPosition until toPosition) {
                    Collections.swap(mCurrentSceneBean.layerList, i, i + 1)
                }
            } else {
                for (i in fromPosition downTo toPosition + 1) {
                    Collections.swap(mCurrentSceneBean.layerList, i, i - 1)
                }
            }
        }
        mCurrentSceneBean.layerList.forEach {
            Log.d("tag", "图层：${it.layerId} ${it.name}")
        }
        mLayerApi!!.orderList.forEach {
            Log.d("tag", "图层2：${it}")
        }
    }

    fun getSelectedLayerVisible(): Boolean {
        return currentSelectedLayer?.config?.visible == 1
    }

    // 当前选中图层是否开启自动抠像功能
    fun getSelectedLayerIsCutBg(): Boolean {
        return mCurrentSelectedLayerBean?.greenSource != 0
    }

    //设置图层可见性 摄像头使用
    fun setSelectedLayerVisible(visible: Boolean) {
        mCurrentSelectedLayerBean?.visible = if (visible) 1 else 0
        currentSelectedLayer?.updateConfig(mapOf<String, Any>("setVisible" to visible))
        updateLocalSceneBean()
    }

    //设置是否抠像
    fun setSelectedVirtualBackground(virtual: Boolean) {
        if (virtual) {
            mCurrentSelectedLayerBean?.virtualBackground = 1
            mCurrentSelectedLayerBean?.greenSource = 1
        } else {
            mCurrentSelectedLayerBean?.virtualBackground = 0
            mCurrentSelectedLayerBean?.greenSource = 0
        }
        currentSelectedLayer?.updateConfig(mapOf<String, Any>("setVirtualBackground" to virtual))
        currentSelectedLayer?.updateConfig(mapOf<String, Any>("setGreenSource" to virtual))
        updateLocalSceneBean()
    }

    //水平镜像
    fun setSelectedHoriMirror() {
        mCurrentSelectedLayerBean?.hormirror = if (mCurrentSelectedLayerBean?.hormirror == 1) {
            0
        } else {
            1
        }
        currentSelectedLayer?.updateConfig(mapOf<String, Any>("setHoriMirror" to (mCurrentSelectedLayerBean?.hormirror == 1)))
        updateLocalSceneBean()
    }

    var moveStepValue: Float = 0.01f

    fun setSelectedRight() {
        val rectF = currentSelectedLayer?.rect
        rectF?.let {
            it.left += moveStepValue
            it.right += moveStepValue
            currentSelectedLayer?.updateConfig(mapOf("setPosition" to it))
            mCurrentSelectedLayerBean?.rect = it
            updateLocalSceneBean()
        }
    }

    fun setSelectedLeft() {
        val rectF = currentSelectedLayer?.rect
        rectF?.let {
            it.left -= moveStepValue
            it.right -= moveStepValue
            currentSelectedLayer?.updateConfig(mapOf("setPosition" to it))
            mCurrentSelectedLayerBean?.rect = it
            updateLocalSceneBean()
        }
    }

    fun setSelectedUp() {
        val rectF = currentSelectedLayer?.rect
        rectF?.let {
            it.top -= moveStepValue
            it.bottom -= moveStepValue
            currentSelectedLayer?.updateConfig(mapOf("setPosition" to it))
            mCurrentSelectedLayerBean?.rect = it
            updateLocalSceneBean()
        }
    }

    fun setSelectedDown() {
        val rectF = currentSelectedLayer?.rect
        rectF?.let {
            it.top += moveStepValue
            it.bottom += moveStepValue
            currentSelectedLayer?.updateConfig(mapOf("setPosition" to it))
            mCurrentSelectedLayerBean?.rect = it
            updateLocalSceneBean()
        }
    }

    fun setSelectedFullScreen() {
        val rectF = RectF(0f, 0f, 1f, 1f)
        currentSelectedLayer?.updateConfig(mapOf("setPosition" to rectF))
        mCurrentSelectedLayerBean?.rect = rectF

        mPreviewAreaFragment?.currentSelectedLayerRemove()// 通知移除当前图层选中框
        updateLocalSceneBean()
    }

    var zoomStepValue: Float = 0.01f

    //缩小
    fun setSelectedZoomOut() {
        val rectF = currentSelectedLayer?.rect
        rectF?.let {
            it.left += zoomStepValue
            it.top += zoomStepValue
            it.right -= zoomStepValue
            it.bottom -= zoomStepValue
            currentSelectedLayer?.updateConfig(mapOf("setPosition" to it))
            mCurrentSelectedLayerBean?.rect = it
            updateLocalSceneBean()
        }
    }

    //放大
    fun setSelectedZoomIn() {
        val rectF = currentSelectedLayer?.rect
        rectF?.let {
            it.left -= zoomStepValue
            it.top -= zoomStepValue
            it.right += zoomStepValue
            it.bottom += zoomStepValue
            currentSelectedLayer?.updateConfig(mapOf("setPosition" to it))
            mCurrentSelectedLayerBean?.rect = it
            updateLocalSceneBean()
        }
    }

    //右旋转
    fun setSelectedRightRotate() {
        var currentRotate: Int = currentSelectedLayer?.config?.rotate ?: 0
        var rotatedNum = (currentRotate + 90) % 360
        currentSelectedLayer?.updateConfig(mapOf<String, Any>("setRotate" to rotatedNum))
        mCurrentSelectedLayerBean?.rotate = rotatedNum
        updateLocalSceneBean()
    }

    //左旋转
    fun setSelectedLeftRotate() {
        var currentRotate: Int = currentSelectedLayer?.config?.rotate ?: 0
        if (currentRotate == 0) {
            currentRotate = 360
        }
        var rotatedNum = (currentRotate - 90) % 360
        currentSelectedLayer?.updateConfig(mapOf<String, Any>("setRotate" to rotatedNum))
        mCurrentSelectedLayerBean?.rotate = rotatedNum
        updateLocalSceneBean()
    }

    //设置透明度
    fun setSelectedAlpha(alpha: Float) {
        mCurrentSelectedLayerBean?.alpha = alpha
        Log.d("tag", "setAlpha: ${mCurrentSelectedLayerBean?.alpha}")
        currentSelectedLayer?.updateConfig(mapOf<String, Any>("setAlpha" to alpha))
        updateLocalSceneBean()
    }

    // ---------当前选中图层的设置与信息获取 end  ------------


    private fun getCameraId(): Int {
        val manager: CameraManager =
            BaseApp.instance.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraId = 1
        try {
            val list = manager.cameraIdList
                ?: return cameraId
            for (i in list.indices) {
                val id = list[i].toInt()
                if (id == 201) {
                    return 201
                }
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
        return cameraId
    }


    //1: 图片，2：相机，3：视频，4：文字，5：APP
    fun changeLayerTypeToEnum(layerType: Int): LayerType {
        when (layerType) {
            1 -> {
                return LayerType.Picture
            }

            2 -> {
                return LayerType.Camera
            }

            3 -> {
                return LayerType.Media
            }

            4 -> {
                return LayerType.Text
            }

            5 -> {
                return LayerType.Surface
            }

            else -> {
                return LayerType.Picture
            }
        }
    }

    fun changeLayerTypeToNum(layerType: LayerType): Int {
        when (layerType) {
            LayerType.Picture -> {
                return 1
            }

            LayerType.Camera -> {
                return 2
            }

            LayerType.Media -> {
                return 3
            }

            LayerType.Text -> {
                return 4
            }

            LayerType.Surface -> {
                return 5
            }

            else -> {
                return 1
            }
        }
    }

    //缩放类型 1：CENTER_CROP，2：FIT_CENTER，3：FIT_XY
    fun changeScaleTypeToInt(scaleType: ScaleType): Int {
        return when (scaleType) {
            ScaleType.CENTER_CROP -> {
                1
            }

            ScaleType.FIT_CENTER -> {
                2
            }

            ScaleType.FIT_XY -> {
                3
            }

            else -> {
                1
            }
        }
    }

    //缩放类型 1：CENTER_CROP，2：FIT_CENTER，3：FIT_XY
    fun changeScaleType(scaleType: Int): ScaleType {
        return when (scaleType) {
            1 -> {
                ScaleType.CENTER_CROP
            }

            2 -> {
                ScaleType.FIT_CENTER
            }

            3 -> {
                ScaleType.FIT_XY
            }

            else -> {
                ScaleType.CENTER_CROP
            }
        }
    }

    //抠像 0：不抠像，1：绿幕抠像 2：蓝幕抠像 3：AI抠像
    fun changeCutTypeToInt(cutType: KeyCutType): Int {
        when (cutType) {
            KeyCutType.Close -> {
                return 0
            }

            KeyCutType.Green -> {
                return 1
            }

            KeyCutType.Blue -> {
                return 2
            }

            KeyCutType.AI -> {
                return 3
            }

            else -> {
                return 0
            }
        }
    }

    //抠像 0：不抠像，1：绿幕抠像 2：蓝幕抠像 3：AI抠像
    fun changeCutType(cutType: Int): KeyCutType {
        when (cutType) {
            0 -> {
                return KeyCutType.Close
            }

            1 -> {
                return KeyCutType.Green
            }

            2 -> {
                return KeyCutType.Blue
            }

            3 -> {
                return KeyCutType.AI
            }

            else -> {
                return KeyCutType.Close
            }
        }
    }

}