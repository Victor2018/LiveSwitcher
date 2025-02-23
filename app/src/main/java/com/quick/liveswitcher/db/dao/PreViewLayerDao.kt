package com.quick.liveswitcher.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.quick.liveswitcher.db.entity.PreViewLayerEntity

@Dao
interface PreViewLayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PreViewLayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(datas: List<PreViewLayerEntity>)

    @Delete
    suspend fun delete(entity: PreViewLayerEntity)

    @Query("DELETE FROM tb_preview_layer where id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM tb_preview_layer")
    suspend fun clearAll()

    @Query("SELECT * FROM tb_preview_layer ORDER BY create_date DESC")
    fun getAllLayer(): LiveData<List<PreViewLayerEntity>>

    @Query("SELECT * FROM tb_preview_layer where layer_type = :layerType")
    fun getAllLayerByType(layerType: Int): LiveData<List<PreViewLayerEntity>>
}