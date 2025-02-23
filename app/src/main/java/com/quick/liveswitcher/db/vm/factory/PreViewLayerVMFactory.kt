package com.quick.liveswitcher.db.vm.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.quick.liveswitcher.db.repository.PreViewLayerRepository
import com.quick.liveswitcher.db.vm.PreViewLayerVM

class PreViewLayerVMFactory(
    private val repository: PreViewLayerRepository,
    owner: SavedStateRegistryOwner,
    private val layerType: Int
) : BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return PreViewLayerVM(repository, layerType)
    }
}