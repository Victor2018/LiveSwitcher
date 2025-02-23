package com.quick.liveswitcher.db.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quick.liveswitcher.db.entity.PreViewLayerEntity
import com.quick.liveswitcher.db.repository.PreViewLayerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreViewLayerVM(
    private val repository: PreViewLayerRepository,
    private val layerType: Int
) : ViewModel() {

    val allLayerData = repository.getAllLayer()

    val allLayerByTypeData = repository.getAllLayerByType(layerType)

    fun insert(data: PreViewLayerEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.insert(data)
            }
        }
    }

    fun delete(data: PreViewLayerEntity) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.delete(data)
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.delete(id)
            }
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.clearAll()
            }
        }
    }

}