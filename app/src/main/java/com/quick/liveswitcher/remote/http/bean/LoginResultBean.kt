package com.dongyou.cloudgame.ui.login.bean
import com.google.gson.annotations.SerializedName


/**
 * @Description
 * @Author Xiang Kejia
 * @Date 2021/8/10 16:48
 */
 data class LoginResultBean(
    @SerializedName("loginStatus")
    val loginStatus: Int?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("userName")
    val userName:String?=null
)