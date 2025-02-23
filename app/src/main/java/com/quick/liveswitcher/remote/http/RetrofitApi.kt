package com.dongyou.cloudgame.app

import com.dongyou.common.http.HttpApi
import com.quick.liveswitcher.remote.http.apiservice.ApiService

class RetrofitApi private constructor(){
    companion object {
        val instance: RetrofitApi by lazy(mode = LazyThreadSafetyMode.NONE) {
            RetrofitApi() }
    }
    /**ApiService*/
    val apiService: ApiService = HttpApi.retrofit.create(ApiService::class.java)

}