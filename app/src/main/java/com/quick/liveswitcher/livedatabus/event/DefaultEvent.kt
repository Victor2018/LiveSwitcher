package com.quick.liveswitcher.livedatabus.event

import androidx.lifecycle.*
import java.util.HashMap

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: DefaultEvent
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 普通事件 一对一
 * -----------------------------------------------------------------
 */

class DefaultEvent(private val liveDataMap: HashMap<String, MutableLiveData<Any?>>,
                   private val tag: String) : Event {


    override fun observe(owner: LifecycleOwner, observer: Observer<Any?>) {
        subscribe(owner, observer)
    }

    override fun observeForever(owner: LifecycleOwner, observer: Observer<Any?>) {
        subscribeForever(owner, observer)
    }

    private fun subscribe(owner: LifecycleOwner, observer: Observer<Any?>) {
        liveDataMap[tag] = MutableLiveData()
        liveDataMap[tag]?.observe(owner, Observer { o ->
            observer.onChanged(o)
        })
    }

    private fun subscribeForever(owner: LifecycleOwner, observer: Observer<Any?>) {
        liveDataMap[tag] = MutableLiveData()

        val mObserver= Observer<Any?> {o ->
            observer.onChanged(o)
        }

        liveDataMap[tag]?.observeForever(mObserver)

        //onDestroy时解绑
        owner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                liveDataMap[tag]?.removeObserver(mObserver)
            }
        })
    }

}