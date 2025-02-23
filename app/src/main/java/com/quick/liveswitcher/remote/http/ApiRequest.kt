package com.dongyou.common.http

import com.quick.liveswitcher.remote.http.bean.HttpResultEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private val mScope: CoroutineScope by lazy {
    CoroutineScope(Dispatchers.Main + Job())
}

/**
 * 接口网络请求
 */
fun <T> requestApi(
    requestBlock: suspend CoroutineScope.() -> HttpResultEntity<T>,
    resultBlock: suspend CoroutineScope.(HttpResultEntity<T>?) -> Unit
): Job {
    return mScope.launch {
        try {
            val response = requestBlock()
            resultBlock(response)
        } catch (e: Exception) {
            val bean = HttpResultEntity<T>()
            bean.code = "-1"
            bean.message = "网络请求异常,请检查网络!"
            resultBlock(bean)
        }
    }
}



