package com.quick.liveswitcher.db.repository

import com.quick.liveswitcher.db.AppDatabase
import com.quick.liveswitcher.db.entity.PreViewLayerEntity

class PreViewLayerRepository constructor(private val db: AppDatabase) {
    val preViewLayerDao = db.preViewLayerDao()

    suspend fun insert(entity: PreViewLayerEntity) {
        preViewLayerDao.insert(entity)
    }

    suspend fun delete(entity: PreViewLayerEntity) {
        preViewLayerDao.delete(entity)
    }

    suspend fun delete(id: Long) {
        preViewLayerDao.delete(id)
    }

    suspend fun clearAll() {
        preViewLayerDao.clearAll()
    }

    fun getAllLayer() = preViewLayerDao.getAllLayer()

    fun getAllLayerByType(layerType: Int) = preViewLayerDao.getAllLayerByType(layerType)

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: PreViewLayerRepository? = null

        fun getInstance(db: AppDatabase) =
                instance ?: synchronized(this) {
                    instance ?: PreViewLayerRepository(db).also { instance = it }
                }
    }
}