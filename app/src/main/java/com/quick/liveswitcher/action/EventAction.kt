package com.quick.liveswitcher.action

object EventAction {
    /**
     * tab 发生切换
     */
    const val TAB_CHANGED = "TAB_CHANGED"

    /**
     * 图层被选中
     */
    const val ON_LAYER_SELECTED = "ON_LAYER_SELECTED"

    /**
     * 没有图层被选中
     */
    const val ON_LAYER_UNSELECTED = "ON_LAYER_UNSELECTED"

    /**
     * 添加图层
     */
    const val ON_LAYER_ADD = "ON_LAYER_ADD"

    /**
     * 图层被移除
     */
    const val ON_LAYER_REMOVE = "ON_LAYER_REMOVE"

    /**
     * 图层层级发生变化
     */
    const val ON_LAYER_HIERARCHY_MOVE = "ON_LAYER_HIERARCHY_MOVE"


    /**
     * 场景正在被选中
     */
    const val ON_SCENE_SELECTING = "ON_SCENE_SELECTING"

    /**
     * 场景被选中
     */
    const val ON_SCENE_SELECTED = "ON_SCENE_SELECTED"

    /**
     * 场景被删除
     */
    const val ON_SCENE_DELETED = "ON_SCENE_DELETED"

    /**
     * APP 被删除
     */
    const val ON_APP_DELETED = "ON_APP_DELETED"

    /**
     * 预览图更新了
     */
    const val SCENE_SHOT_UPDATE = "SCENE_SHOT_UPDATE"
}