package com.quick.liveswitcher.db.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quick.liveswitcher.data.scene.SceneBean
import com.quick.liveswitcher.db.repository.SceneRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SceneVM(
    private val repository: SceneRepository
) : ViewModel() {

    val allSceneData = repository.getAllScene()

    fun insert(data: SceneBean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.insert(data)
            }
        }
    }

    fun updateLayerList(layerListStr: String, id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateLayerList(layerListStr, id)
            }
        }
    }

    fun updateAppList(appListStr: String, id: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateAppList(appListStr, id)
            }
        }
    }

    fun delete(data: SceneBean) {
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

    fun deleteSceneBySceneName(sceneName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.deleteSceneBySceneName(sceneName)
            }
        }
    }

    fun updateSceneBySceneName(sceneName: String, isSelected: Boolean, previewScreenShotIvPath: String, layerListJsonStr: String, appListJsonStr: String){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.updateSceneBySceneName(sceneName, isSelected, previewScreenShotIvPath, layerListJsonStr, appListJsonStr)
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