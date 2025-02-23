package com.quick.liveswitcher.remote.http.bean

import java.io.Serializable


data class CommonRequestBean<T>(
    val header : RequestHeaderBean,
    val body : T
):Serializable