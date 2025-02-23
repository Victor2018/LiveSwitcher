package com.quick.liveswitcher.db.vm.factory

import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.quick.liveswitcher.db.repository.SceneRepository
import com.quick.liveswitcher.db.vm.SceneVM

class SceneVMFactory(
    private val repository: SceneRepository,
    owner: SavedStateRegistryOwner
) : BaseVMFactory(owner) {
    override fun getVM(): ViewModel {
        return SceneVM(repository)
    }
}