package com.quick.liveswitcher.remote.http.apiservice

import com.dongyou.cloudgame.ui.login.bean.LoginResultBean
import com.quick.liveswitcher.data.CheckUpdateParm
import com.quick.liveswitcher.data.LatestVersionData
import com.quick.liveswitcher.remote.http.bean.HttpResultEntity
import retrofit2.http.Body
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("outer/user/dyLogin/msgLogin") //短信验证码登录
    suspend fun msgLogin(@FieldMap params: @JvmSuppressWildcards Map<String, Any>): HttpResultEntity<LoginResultBean>

    @POST("apiapppre/users/seederUpGrade") //短信验证码登录
    suspend fun checkAppUpdate(@Header("Content-Type") contentType: String,
                               @Body body: CheckUpdateParm
    ): HttpResultEntity<LatestVersionData>



}