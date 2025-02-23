package com.quick.liveswitcher.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.quick.liveswitcher.data.scene.SceneBean

@Dao
interface SceneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SceneBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(datas: List<SceneBean>)

    @Delete
    suspend fun delete(entity: SceneBean)

    @Query("UPDATE tb_scene SET layerListStr = :layerListStr WHERE id = :id")
    fun updateLayerList(layerListStr: String, id: String)

    @Query("UPDATE tb_scene SET appListStr = :appListStr WHERE id = :id")
    fun updateAppList(appListStr: String, id: String)

    @Query("DELETE FROM tb_scene where id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM tb_scene where sceneName = :sceneName")
    suspend fun deleteSceneBySceneName(sceneName: String)

    @Query("UPDATE tb_scene SET isSelected = :isSelected, previewScreenShotIvPath = :previewScreenShotIvPath, layerListStr = :layerListJsonStr, appListStr = :appListJsonStr WHERE sceneName = :sceneName")
    suspend fun updateSceneBySceneName(sceneName: String, isSelected: Boolean, previewScreenShotIvPath: String, layerListJsonStr: String, appListJsonStr: String)

    @Query("UPDATE tb_scene SET isSelected = :isSelected WHERE id = :sceneName")
    suspend fun updateSceneSelectedStatus(isSelected: Boolean, sceneName: String)

    @Query("DELETE FROM tb_scene")
    suspend fun clearAll()

    @Query("SELECT * FROM tb_scene ORDER BY create_date DESC")
    fun getAllScene(): LiveData<List<SceneBean>>

    @Query("SELECT * FROM tb_scene ORDER BY create_date ")
    suspend fun getAllSceneFromDb(): List<SceneBean>
}