package com.quick.liveswitcher.livedatabus

import androidx.lifecycle.MutableLiveData
import com.quick.liveswitcher.livedatabus.event.*

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: LiveDataBus
 * Author: Victor
 * Date: 2022/5/19 18:30
 * Description: 
 * -----------------------------------------------------------------
 */

object LiveDataBus {
    const val TAG = "LiveDataBus"

    private val liveDataMap = HashMap<String, MutableLiveData<Any?>>()
    private val liveDataMaps = HashMap<String, HashMap<String, MutableLiveData<Any?>>>()

    /**
     * 关联一个普通事件（一对一）消息，需要关联后才能收到发送的消息
     * @param event 事件标识
     */
    fun with(event: Int): Event {
        return with(event.toString())
    }

    /**
     * 关联一个普通事件消息，需要关联后才能收到发送的消息
     * @param event 事件标识
     */
    fun with(event: String): Event {
        return DefaultEvent(liveDataMap, event)
    }

    /**
     * 关联一个一对多事件消息，需要关联后才能收到发送的消息
     * @param event 事件标识
     * @param tag 事件接受方标识
     */
    fun withMulti(event: Int, tag: String): Event {
        return withMulti(event.toString(), tag)
    }

    /**
     * 关联一个一对多事件消息，需要关联后才能收到发送的消息
     * @param event 事件标识
     * @param tag 事件接受方标识
     */
    fun withMulti(event: String, tag: String): Event {
        return MultiEvent(liveDataMaps, event, tag)
    }

    /**
     * 关联一个粘性事件消息，可以收到关联之前发送的消息
     * @param event 事件标识
     */
    fun withStickiness(event: Int): Event {
        return withStickiness(event.toString())
    }

    /**
     * 关联一个粘性事件消息，可以收到关联之前发送的消息
     * @param event 事件标识
     */
    fun withStickiness(event: String): Event {
        return StickinessEvent(liveDataMap, event)
    }

    /**
     * 关联一个一对多粘性事件消息，可以收到关联之前发送的消息
     * @param event 事件标识
     * @param tag 事件接受方标识
     */
    fun withMultiStickiness(event: Int, tag: String): Event {
        return withMultiStickiness(event.toString(), tag)
    }

    /**
     * 关联一个一对多粘性事件消息，可以收到关联之前发送的消息
     * @param event 事件标识
     * @param tag 事件接受方标识
     */
    fun withMultiStickiness(event: String, tag: String): Event {
        return MultiStickinessEvent(liveDataMaps, event, tag)
    }

    /**
     * 发送一个普通事件消息
     * @param event 事件标识
     * @param t 事件内容
     */
    fun send(event: Int, t: Any? = null) {
        send(event.toString(), t)
    }

    /**
     * 发送一个普通事件消息
     * @param event 事件标识
     * @param t 事件内容
     */
    fun sendMulti(event: Int, t: Any? = null) {
        sendMulti(event.toString(), t)
    }

    /**
     * 发送一个普通事件消息
     * @param event 事件标识
     * @param t 事件内容
     */
    fun send(event: String, t: Any? = null) {
        if (!liveDataMap.containsKey(event) || liveDataMap[event] == null) return
        liveDataMap[event]?.postValue(t)
    }

    /**
     * 发送一个普通事件消息
     * @param event 事件标识
     * @param t 事件内容
     */
    fun sendMulti(event: String, t: Any? = null) {
        if (!liveDataMaps.containsKey(event) || liveDataMaps[event] == null) return

        liveDataMaps[event]?.forEach { (key, value) ->
            value.postValue(t)
        }
    }

    /**
     * 发送一个普通粘性（一对一）事件消息
     *  @param event 事件标识
     *  @param t 事件内容
     */
    fun sendStickiness(event: Int, t: Any? = null) {
        return sendStickiness(event.toString(), t)
    }

    /**
     * 发送一个普通粘性（一对一）事件消息
     *  @param event 事件标识
     *  @param t 事件内容
     */
    fun sendStickiness(event: String, t: Any? = null) {
        if (!liveDataMap.containsKey(event) || liveDataMap[event] == null) {
            liveDataMap[event] = MutableLiveData()
        }
        liveDataMap[event]?.postValue(t)
    }

    /**
     * 发送一个一对多粘性事件消息
     *  @param event 事件标识
     *  @param t 事件内容
     */
    fun sendMultiStickiness(event: Int, t: Any? = null) {
        return sendMultiStickiness(event.toString(), t)
    }

    /**
     * 发送一个一对多粘性事件消息
     *  @param event 事件标识
     *  @param t 事件内容
     */
    fun sendMultiStickiness(event: String, t: Any? = null) {
        if (!liveDataMaps.containsKey(event) || liveDataMaps[event] == null) {
            liveDataMaps[event] = HashMap<String, MutableLiveData<Any?>>()
        }

        liveDataMaps[event]?.forEach { (key, value) ->
            value.postValue(t)
        }
    }

}