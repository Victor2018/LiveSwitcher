package com.quick.liveswitcher.db.entity

import android.graphics.RectF

data class LayerEntity (
    var name: String = "",
    var rect: RectF = RectF(),
    var positionRect: RectF = RectF(), //位置
    var layerId: Int = 0,
    var layerType: Int = 1, //1: 图片，2：相机，3：视频，4：文字，5：APP
    var visible: Int = 1, //0: 不可见，1：可见
    var rotate:Int = 0, //旋转度数
    var hormirror:Int = 0, //水平镜像 0: 不镜像，1：镜像
    var vermirror:Int = 0, //垂直镜像 0: 不镜像，1：镜像
    var alpha:Float = 1f, //透明度
    var cutType:Int = 1, //抠像 如果后续需要开启绿幕抠图，初始化的时候必须设置为1 0：不抠像，1：绿幕抠像 2：蓝幕抠像 3：AI抠像
    var virtualBackground: Int = 0, //开启图层背景替换 0：不开启，1：开启
    var greenSource:Int = 0, //当前信号源为绿幕或蓝幕资源时开启有效，开启后将绿幕或蓝幕背景抠除，将当前图层的下一图层画面透出来 0: 不开启，1：开启
    var cutprotectlevel:Float = 1f, //在绿幕抠图时，偏色保护强度调节，主要是黄色保护
    var cutLevel:Float = 1f, //设置背景抠图的强度[0.0-1.0]，图层背景替换开启时生效
    var scaleType:Int = 2, //缩放类型 1：CENTER_CROP，2：FIT_CENTER，3：FIT_XY
    var volume:Float = 1f, //音量
    var materialPath:String = "", //图片、视频路径
    var appPackageName:String = "", //app包名
    var isbeauty:Int = 0, //是否开启美颜 0：不开启，1：开启
    var appIconName: String = "" //Icon缩略图
)