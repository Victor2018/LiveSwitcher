package com.dongyou.common.storage.mmkv

import com.tencent.mmkv.MMKV

/**
 * @Description
 * @Author Xiang Kejia
 * @Date 2021/7/3 15:33
 */

private val mk: MMKV = MMKV.defaultMMKV()

fun mkCacheBoolean(key: String, value: Boolean) {
    mk.encode(key, value)
}

fun mkRemove(key:String){
    mk.removeValueForKey(key)
}
fun mkGetBoolean(key: String): Boolean {
    return mk.decodeBool(key)
}

fun mkCacheString(key: String, value: String?) {
    mk.encode(key, value)
}

fun mkGetString(key: String): String? {
    return mk.decodeString(key)
}


fun mkCacheInt(key: String, value: Int) {
    mk.encode(key, value)
}

fun mkGetInt(key: String): Int {
    return mk.decodeInt(key)
}

fun mkCacheFloat(key: String, value: Float) {
    mk.encode(key, value)
}

fun mkGetFloat(key: String): Float {
    return mk.decodeFloat(key)
}

fun mkCacheDouble(key: String, value: Double) {
    mk.encode(key, value)
}

fun mkGetDouble(key: String): Double {
    return mk.decodeDouble(key)
}

fun mkCacheLong(key: String, value: Long) {
    mk.encode(key, value)
}

fun mkGetLong(key: String): Long {
    return mk.decodeLong(key)
}









