package com.quick.liveswitcher.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "tb_preview_layer")
data class PreViewLayerEntity(
    @ColumnInfo(name = "layer_type") var layerType: Int,//图层类型
    @ColumnInfo(name = "layer_name") var layerName: String,//图层名称
    @ColumnInfo(name = "layer_data") var layerData: String,//图层排序
    @ColumnInfo(name = "order") var order: Int,//图层排序
    @ColumnInfo(name = "create_date") val createDate: Calendar = Calendar.getInstance()
) {
    @PrimaryKey(autoGenerate = true) var id: Long = 0
}