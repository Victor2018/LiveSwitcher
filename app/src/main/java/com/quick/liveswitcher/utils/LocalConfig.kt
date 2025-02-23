package com.quick.liveswitcher.utils

import android.text.TextUtils
import com.quick.liveswitcher.data.scene.SceneBean

object LocalConfig {

    fun setSceneData(data: List<SceneBean>?) {
        SharedPreferencesUtils.sceneData = JsonUtils.toJSONString(data)
    }

    fun getUserInfo(): List<SceneBean>? {
        val sceneDataStr = SharedPreferencesUtils.sceneData
        if (!TextUtils.isEmpty(sceneDataStr)) {
            return JsonUtils.parseArray(sceneDataStr, SceneBean::class.java)
        }
        return null
    }
}