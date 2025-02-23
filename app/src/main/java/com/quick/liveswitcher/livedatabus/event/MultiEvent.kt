package com.quick.liveswitcher.livedatabus.event

import androidx.lifecycle.*
import java.util.HashMap

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2018-2028, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: MultiEvent
 * Author: Victor
 * Date: 2022/3/7 20:18
 * Description: 一对多
 * -----------------------------------------------------------------
 */

class MultiEvent(private val liveDataMaps: HashMap<String, HashMap<String,MutableLiveData<Any?>>>,
                 private val event: String,
                 private val tag: String) : Event {

    override fun observe(owner: LifecycleOwner, observer: Observer<Any?>) {
        subscribe(owner, observer)
    }

    override fun observeForever(owner: LifecycleOwner, observer: Observer<Any?>) {
        subscribeForever(owner, observer)
    }

    private fun subscribe(owner: LifecycleOwner, observer: Observer<Any?>) {
        if (liveDataMaps[event] == null) {
            liveDataMaps[event] = HashMap()
        }

        liveDataMaps[event]?.set(tag, MutableLiveData())

        liveDataMaps[event]?.get(tag)?.observe(owner, Observer { o ->
            observer.onChanged(o)
        })
    }

    private fun subscribeForever(owner: LifecycleOwner, observer: Observer<Any?>) {
        if (liveDataMaps[event] == null) {
            liveDataMaps[event] = HashMap()
        }

        liveDataMaps[event]?.set(tag, MutableLiveData())

        val mObserver= Observer<Any?> {o ->
            observer.onChanged(o)
        }

        liveDataMaps[event]?.get(tag)?.observeForever(mObserver)

        //onDestroy时解绑
        owner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                liveDataMaps[event]?.get(tag)?.removeObserver(mObserver)
            }
        })
    }
}