package com.quick.liveswitcher.remote.http.bean


open class HttpResultEntity<T>{
    var code: String? = "" //200表示成功，-1表示非接正常正常请求如404，504，其他则是接口正常请求结果，如API110001
    var message: String? = ""
    var linkUrl: String? = ""
    var data: T? = null
    override fun toString(): String {
        return "HttpResultEntity(code=$code, message=$message, linkUrl=$linkUrl, data=$data)"
    }
}
