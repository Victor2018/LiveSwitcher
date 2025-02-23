package com.quick.liveswitcher.local

import android.graphics.Bitmap
import com.quick.liveswitcher.BaseApp
import com.quick.liveswitcher.action.EventAction
import com.quick.liveswitcher.data.scene.SceneBean
import com.quick.liveswitcher.ui.bottomfragment.scene.SceneDataManager
import com.quick.liveswitcher.utils.FileUtil
import com.quick.liveswitcher.livedatabus.LiveDataBus
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

object FileManager {


    fun init() {
        Thread {
            FileUtil.copyPresetMaterialToFile(BaseApp.instance)
        }.start()
    }

    fun savePreviewShotBitmap(bitmap: Bitmap, sceneBean: SceneBean) {
        //先删除以前的图片 然后更新的图片
        val oldPath = sceneBean.previewScreenShotIvPath
        val oldFile = File(oldPath)
        if (oldFile.isFile) {
            oldFile.delete()
        }
        val fileName = SceneDataManager.getCurrentScene().sceneName + "_" + System.currentTimeMillis() + ".png"
        val file = saveBitmapToStorage(fileName, bitmap)
        if (file != null) {
            SceneDataManager.getCurrentScene().previewScreenShotIvPath = file.path
            LiveDataBus.sendMulti(EventAction.SCENE_SHOT_UPDATE, sceneBean)
        }
    }

    fun saveBitmapToStorage(fileName: String, bitmap: Bitmap): File? {
        val storageDir = FileUtil.getSceneShotDir(BaseApp.instance)
        val storageFolder = File(storageDir)
        if (!storageFolder.exists()) {
            storageFolder.mkdirs()
        }
        val imageFile = File(storageDir, fileName)
        if (imageFile.exists()) {
            imageFile.delete()
        }
        try {
            val stream: OutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) // 保存Bitmap为PNG格式
            stream.flush()
            stream.close()
            return imageFile
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }


}