package com.quick.liveswitcher.db.repository

import com.quick.liveswitcher.data.scene.SceneBean
import com.quick.liveswitcher.db.AppDatabase

class SceneRepository constructor(private val db: AppDatabase) {
    val sceneDao = db.sceneDao()

    suspend fun insert(entity: SceneBean) {
        sceneDao.insert(entity)
    }

    suspend fun delete(entity: SceneBean) {
        sceneDao.delete(entity)
    }

    suspend fun updateLayerList(layerListStr: String, id: String) {
        sceneDao.updateLayerList(layerListStr,id)
    }

    suspend fun updateAppList(appListStr: String, id: String) {
        sceneDao.updateAppList(appListStr,id)
    }

    suspend fun delete(id: Long) {
        sceneDao.delete(id)
    }

    suspend fun deleteSceneBySceneName(sceneName: String) {
        sceneDao.deleteSceneBySceneName(sceneName)
    }

    suspend fun updateSceneBySceneName(sceneName: String, isSelected: Boolean, previewScreenShotIvPath: String, layerListJsonStr: String, appListJsonStr: String){
        sceneDao.updateSceneBySceneName(sceneName,isSelected,previewScreenShotIvPath,layerListJsonStr,appListJsonStr)
    }

    suspend fun clearAll() {
        sceneDao.clearAll()
    }

    fun getAllScene() = sceneDao.getAllScene()


    companion object {

        // For Singleton instantiation
        @Volatile private var instance: SceneRepository? = null

        fun getInstance(db: AppDatabase) =
                instance ?: synchronized(this) {
                    instance ?: SceneRepository(db).also { instance = it }
                }
    }
}