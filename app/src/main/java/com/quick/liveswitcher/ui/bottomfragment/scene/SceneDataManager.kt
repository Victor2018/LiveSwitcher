package com.quick.liveswitcher.ui.bottomfragment.scene

import android.graphics.RectF
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global
import android.text.TextUtils
import android.transition.Scene
import android.util.Log
import com.quick.liveswitcher.BaseApp
import com.quick.liveswitcher.action.EventAction
import com.quick.liveswitcher.data.scene.AppBean
import com.quick.liveswitcher.data.scene.LayerBean
import com.quick.liveswitcher.data.scene.SceneBean
import com.quick.liveswitcher.db.AppDatabase
import com.quick.liveswitcher.db.dao.SceneDao
import com.quick.liveswitcher.ui.previewarea.LayerController
import com.quick.liveswitcher.livedatabus.LiveDataBus
import com.quick.liveswitcher.ui.previewarea.PreviewAreaFragment
import com.quick.liveswitcher.utils.JsonUtils
import com.quick.liveswitcher.utils.ToastUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

object SceneDataManager {
    private var mSceneList: ArrayList<SceneBean> = ArrayList() //场景中的视图层
    private var mCurrentSceneBean: SceneBean? = null
    private var mHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var arrayInt: ArrayList<Int>
    private var mPreviewAreaFragment: PreviewAreaFragment? = null
    private lateinit var mSceneDao: SceneDao

    private var mScanModeSceneBean: SceneBean? = null //扫码专用模式下的对象
    var isScanMode = false //当前是不是扫码模式


    fun initSceneData(fragment: PreviewAreaFragment) {
        GlobalScope.launch(context = Dispatchers.Main) {
            mPreviewAreaFragment = fragment
            arrayInt = createSequentialArrayList()

            getScanModeSceneBean()

            val db = AppDatabase.getInstance(BaseApp.instance)
            mSceneDao = db.sceneDao()

            val sceneList = getSceneFromDb()
            if (sceneList.size == 0) {
                val sceneBean = initDefaultScene("场景1")
                mSceneList.add(sceneBean)
                addSceneToDb(sceneBean)
            } else {
                mSceneList = sceneList
            }
            mCurrentSceneBean = getCurrentScene()
            LayerController.onSceneSelected(mCurrentSceneBean!!)
            delayPreviewScreenShot()
        }
    }

    fun getAllSceneList(): ArrayList<SceneBean> {
        return mSceneList
    }

    fun createSequentialArrayList(): ArrayList<Int> {
        val arrayList = ArrayList<Int>()
        for (i in 1..1000) {
            arrayList.add(i)
        }
        return arrayList
    }

    fun addScene() {
        clearSelectedStatus()
        getNewSceneName()

        val sceneBean: SceneBean = initDefaultScene(getNewSceneName())
        mSceneList.add(sceneBean)
        mCurrentSceneBean = getCurrentScene()
        addSceneToDb(sceneBean)
        LayerController.onSceneSelected(mCurrentSceneBean!!)
        delayPreviewScreenShot()
    }

    fun switchScene(sceneBean: SceneBean, isScanMode: Boolean = false) {
        previewScreenShot()
        clearSelectedStatus()
        this.isScanMode = isScanMode
        mSceneList.forEach {
            if (sceneBean == it) {
                it.isSelected = true
            }
        }
        if (isScanMode) {
            mScanModeSceneBean!!.isSelected = true
            mCurrentSceneBean = mScanModeSceneBean
        } else {
            mScanModeSceneBean!!.isSelected = false
            mCurrentSceneBean = getCurrentScene()
        }

        LayerController.onSceneSelected(mCurrentSceneBean!!)
    }

    private fun clearSelectedStatus() {
        isScanMode = false
        mCurrentSceneBean?.isSelected = false
        mCurrentSceneBean = null
        mSceneList.forEach {
            it.isSelected = false
        }
    }


    fun getScanModeSceneBean(): SceneBean {
        if(mScanModeSceneBean == null){
            mScanModeSceneBean = initScanModeSceneBean()
        }
        return mScanModeSceneBean!!
    }

    private fun initScanModeSceneBean():SceneBean {
        val scanModeBean = SceneBean()
        scanModeBean.sceneName = "scan"
        scanModeBean.isSelected = false
        scanModeBean.appIconName = "scene_icon_scan"
        val layerList: ArrayList<LayerBean> = ArrayList()
        scanModeBean.layerList = layerList

        val cameraLayerBean = getDefaultCameraLayerBean()
        layerList.add(cameraLayerBean)

        return scanModeBean
    }


    //初始化默认场景
    private fun initDefaultScene(sceneName: String): SceneBean {
        val sceneBean = SceneBean()
        sceneBean.sceneName = sceneName
        sceneBean.isSelected = true
        val layerList: ArrayList<LayerBean> = ArrayList()
        sceneBean.layerList = layerList

        val cameraLayerBean = getDefaultCameraLayerBean()
        layerList.add(cameraLayerBean)

        return sceneBean
    }

    //获取新的场景名
    private fun getNewSceneName(): String {
        val sceneNumList = ArrayList<Int>()
        val pattern = Pattern.compile("\\d+")
        mSceneList.forEach {
            val matcher = it.sceneName?.let { it1 -> pattern.matcher(it1) }
            Log.d("tag", "sceneName:${it.sceneName}")
            if (matcher?.find() == true) {
                val number = matcher.group().toInt()
                Log.d("tag", "number:$number")
                sceneNumList.add(number)
            }
        }
        return "场景" + findAndSortUniqueNumbers(arrayInt, sceneNumList)
    }

    fun findAndSortUniqueNumbers(arr1: ArrayList<Int>, arr2: ArrayList<Int>): Int {
        // 将两个数组转换为 HashSet
        val set1 = arr1.toSet()
        val set2 = arr2.toSet()

        // 找出在 arr1 中但不在 arr2 中的数
        val uniqueInArr1 = set1 - set2

        // 找出在 arr2 中但不在 arr1 中的数
        val uniqueInArr2 = set2 - set1

        // 合并两个差集的结果
        val uniqueNumbers = uniqueInArr1 + uniqueInArr2

        // 将结果排序
        val resultList = uniqueNumbers.sorted()
        return resultList[0]
    }

    fun updateScene(sceneBean: SceneBean) {
        GlobalScope.launch(context = Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                sceneBean.appListJsonStr = JsonUtils.toJSONString(sceneBean.appList)
                sceneBean.layerListJsonStr = JsonUtils.toJSONString(sceneBean.layerList)
                mSceneDao.updateSceneBySceneName(sceneBean.sceneName, sceneBean.isSelected, sceneBean.previewScreenShotIvPath, sceneBean.layerListJsonStr, sceneBean.appListJsonStr)
            }
        }
    }


    fun getCurrentScene(): SceneBean {
        var currentScene: SceneBean? = mCurrentSceneBean
        if (currentScene != null) {
            return currentScene
        }
        for (scene in mSceneList) {
            if (scene.isSelected) {
                currentScene = scene
            }
        }
        if (currentScene == null) { //如果没有选择场景，则默认选择第一个场景
            val firstSceneBean = mSceneList[0]
            firstSceneBean.isSelected = true
            currentScene = firstSceneBean
        }
        return currentScene
    }

    //删除app
    fun deleteApp(appBean: AppBean?) {
        if (appBean?.isSelected == true) {
            val layerId = getLayerIdByPackageName(appBean.appPackageName)
            if (layerId != null) {
                LayerController.removeLayer(layerId)
            }
        }
        postAppDeleted()
        mCurrentSceneBean?.appList?.removeAll { it.appPackageName == appBean?.appPackageName }
    }

    //删除场景
    fun deleteScene(sceneBean: SceneBean) {
        if (mSceneList.size == 1) {
            ToastUtils.show("至少保留一个场景")
            return
        }
        if (sceneBean == mCurrentSceneBean) {
            var nextBean: SceneBean? = null
            if (mSceneList[0] == sceneBean) {
                nextBean = mSceneList[1]
            } else {
                nextBean = mSceneList[0]
            }
            switchScene(nextBean)
        }

        deleteDbSceneBySceneName(sceneBean.sceneName)
        mSceneList.remove(sceneBean)
        LiveDataBus.sendMulti(EventAction.ON_SCENE_DELETED)
    }

    fun getDefaultCameraLayerBean(): LayerBean {
        val cameraLayerBean = LayerBean()
        cameraLayerBean.layerType = 2
        cameraLayerBean.name = "camera"
        cameraLayerBean.rect = RectF(0f, 0f, 1f, 1f)
        cameraLayerBean.alpha = 1f
        cameraLayerBean.scaleType = 2
        cameraLayerBean.cutType = 1
        cameraLayerBean.virtualBackground = 0
        cameraLayerBean.greenSource = 0
        cameraLayerBean.cutprotectlevel = 1f
        cameraLayerBean.cutLevel = 0.5f
        cameraLayerBean.hormirror = 1
        cameraLayerBean.appIconName = "camera_icon"
        return cameraLayerBean
    }

    fun getLayerIdByPackageName(packageName: String?): Int? {
        if (TextUtils.isEmpty(packageName)) {
            return null
        }
        var layerId: Int? = null
        mCurrentSceneBean?.layerList?.forEach {
            if (it.appPackageName == packageName) {
                layerId = it.layerId
            }
        }
        return layerId
    }

    fun getDefaultAppLayerBean(appItem: AppBean): LayerBean {
        //添加背景图层
        var layerBean = LayerBean()
        layerBean.layerType = 5
        layerBean.name = appItem.appName
        layerBean.rect = RectF(0f, 0f, 1f, 1f)
        layerBean.alpha = 1f
        layerBean.visible = 1
        layerBean.virtualBackground = 0 //如果要默认设置不抠像，则将virtualBackground 和 greenSource 都置为0
        layerBean.greenSource = 0
        layerBean.cutprotectlevel = 1f
        layerBean.cutLevel = 0.5f
        layerBean.appPackageName = appItem.appPackageName ?: ""
        layerBean.appIconName = appItem.appIconName ?: ""
        layerBean.scaleType = 2
        return layerBean
    }


    fun addAppToBtmList(appItem: AppBean): Boolean {
        if (!checkIsAddedToAppList(appItem)) {
            mCurrentSceneBean?.appList?.add(appItem)
            return true
        }
        return false
    }

    //检查是否已经添加到appList
    private fun checkIsAddedToAppList(appItem: AppBean): Boolean {
        for (app in mCurrentSceneBean?.appList!!) {
            if (app.appPackageName == appItem.appPackageName) {
                return true
            }
        }
        return false
    }


    fun delayPreviewScreenShot() {
        mHandler.postDelayed(object : Runnable {
            override fun run() {
                previewScreenShot()
            }
        }, 3000)
    }

    // 预览截图
    fun previewScreenShot() {
        mCurrentSceneBean?.let {
            mPreviewAreaFragment?.toPreviewScreenShot(it)
            updateScene(it)
        }

    }

    fun postAppDeleted() {
        mCurrentSceneBean?.let { updateDbScene(it) }
        LiveDataBus.sendMulti(EventAction.ON_APP_DELETED)
    }

    // ------------- 场景 数据库操作 start-------------

    fun updateDbSceneSelectedStatus(sceneName: String) {
        GlobalScope.launch(context = Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                for (scene in mSceneList) {
                    if (scene.sceneName == sceneName) {
                        scene.isSelected = true
                        mSceneDao.updateSceneSelectedStatus(true, scene.sceneName)
                    } else {
                        scene.isSelected = false
                        mSceneDao.updateSceneSelectedStatus(false, scene.sceneName)
                    }
                }
            }
        }
    }

    fun updateDbScene(sceneBean: SceneBean) {
        GlobalScope.launch(context = Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                sceneBean.appListJsonStr = JsonUtils.toJSONString(sceneBean.appList)
                sceneBean.layerListJsonStr = JsonUtils.toJSONString(sceneBean.layerList)
                mSceneDao.updateSceneBySceneName(sceneBean.sceneName, sceneBean.isSelected, sceneBean.previewScreenShotIvPath, sceneBean.layerListJsonStr, sceneBean.appListJsonStr)
            }
        }
    }

    fun deleteDbSceneBySceneName(sceneName: String) {
        GlobalScope.launch(context = Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                mSceneDao.deleteSceneBySceneName(sceneName)
            }
        }
    }

    fun addSceneToDb(sceneBean: SceneBean) {
        GlobalScope.launch(context = Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                sceneBean.appListJsonStr = JsonUtils.toJSONString(sceneBean.appList)
                sceneBean.layerListJsonStr = JsonUtils.toJSONString(sceneBean.layerList)
                mSceneDao.insert(sceneBean)
            }
        }
    }

    suspend fun getSceneFromDb(): ArrayList<SceneBean> {
        val sceneList = mSceneDao.getAllSceneFromDb()
        for (scene in sceneList) {
            scene.layerList = JsonUtils.parseArray(scene.layerListJsonStr, LayerBean::class.java) ?: ArrayList()
            scene.appList = JsonUtils.parseArray(scene.appListJsonStr, AppBean::class.java) ?: ArrayList()
        }
        return ArrayList(sceneList) as? ArrayList<SceneBean> ?: ArrayList<SceneBean>();
    }

    // ------------- 场景 数据库操作 end-------------


    //        val dir = FileUtil.getPresetMaterialDir(BaseApp.instance)
    //添加背景图层
//        val bgFile = dir + "background/8.jpg"
//        LayerController.addBgLayer(bgFile)

    //支持视频mp4(非hevc)，mov, 音频mp3 wav m4a（音频时，所有显示类参数如坐标位置透明度等均无效果）//
//        var videoFilePath: String = dir + "media/m3.mp4"
//        UIController.addVideoLayer(videoFilePath)

    //跑马灯图层
//        UIController.addMarqueeTextLayer("文字")

    //添加App图层
//        LayerController.addAppLayer("com.zmwan.live","算粒智播","app_icon_slzb")

    //添加图片图层
//        val picFilePath: String = dir + "sticker/1.png"
//        LayerController.addPicLayer(picFilePath)

    //添加camera图层
//        LayerController.addCameraLayer()


}