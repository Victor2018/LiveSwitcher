package com.quick.liveswitcher.remote.http.bean

data class RequestHeaderBean(
    val appId: String,
    val deviceType: Int,
    val keepid: String,
    val sendTime: Long,
    val versionCode: Int,
    val versionName: String,
    val uuid: String?,
    val dk : String?
)