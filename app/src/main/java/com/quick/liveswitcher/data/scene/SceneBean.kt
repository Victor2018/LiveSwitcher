package com.quick.liveswitcher.data.scene

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.quick.liveswitcher.utils.JsonUtils
import java.util.Calendar

@Entity(tableName = "tb_scene")
data class SceneBean(
    @ColumnInfo(name = "sceneName") var sceneName: String,//场景名称
    @ColumnInfo(name = "isSelected") var isSelected: Boolean,//是否被选中
    @ColumnInfo(name = "appIconName") var appIconName: String,//场景中的扫码与加号
    @ColumnInfo(name = "previewScreenShotIvPath") var previewScreenShotIvPath: String,//预览截图
    @ColumnInfo(name = "layerListStr") var layerListJsonStr: String, //场景中的视图层
    @ColumnInfo(name = "appListStr") var appListJsonStr: String, //场景中已经添加的应用
    @ColumnInfo(name = "create_date") var createDate: Calendar = Calendar.getInstance()
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
    constructor():this("",false,"","","","")
    @Ignore
    var layerList: ArrayList<LayerBean> = JsonUtils.parseArray(layerListJsonStr, LayerBean::class.java) ?: ArrayList() //场景中的视图层
    @Ignore
    var appList: ArrayList<AppBean> = JsonUtils.parseArray(appListJsonStr, AppBean::class.java) ?: ArrayList() //场景中的视图层
}