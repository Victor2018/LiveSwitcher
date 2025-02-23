package com.quick.liveswitcher.db

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.quick.liveswitcher.db.vm.factory.PreViewLayerVMFactory
import com.quick.liveswitcher.db.repository.PreViewLayerRepository
import com.quick.liveswitcher.db.repository.SceneRepository
import com.quick.liveswitcher.db.vm.factory.SceneVMFactory

object DBInjector {

    fun getPreViewLayerRepository(context: Context): PreViewLayerRepository {
        return PreViewLayerRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext))
    }

    fun providePreViewLayerVMFactory(activity: AppCompatActivity, layerType: Int): PreViewLayerVMFactory {
        return PreViewLayerVMFactory(getPreViewLayerRepository(activity),activity,layerType)
    }

    fun providePreViewLayerVMFactory(fragment: Fragment, layerType: Int): PreViewLayerVMFactory {
        return PreViewLayerVMFactory(getPreViewLayerRepository(fragment.requireContext()),fragment,layerType)
    }

    fun getSceneRepository(context: Context): SceneRepository {
        return SceneRepository.getInstance(
            AppDatabase.getInstance(context.applicationContext))
    }

    fun provideSceneVMFactory(activity: AppCompatActivity): SceneVMFactory {
        return SceneVMFactory(getSceneRepository(activity),activity)
    }

    fun provideSceneVMFactory(fragment: Fragment): SceneVMFactory {
        return SceneVMFactory(getSceneRepository(fragment.requireContext()),fragment)
    }

}