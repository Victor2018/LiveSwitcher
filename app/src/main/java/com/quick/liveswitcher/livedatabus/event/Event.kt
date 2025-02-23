package com.quick.liveswitcher.livedatabus.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer

/*
 * -----------------------------------------------------------------
 * Copyright (C) 2020-2080, by Victor, All rights reserved.
 * -----------------------------------------------------------------
 * File: Event
 * Author: Victor
 * Date: 2022/3/1 18:28
 * Description: 
 * -----------------------------------------------------------------
 */

interface Event {
    /**
     * 仅更新处于活动生命周期状态的应用程序组件观察者
     */
    fun observe(owner: LifecycleOwner, observer: Observer<Any?>)

    /**
     * 不受生命周期的影响，只要数据更新就会收到通知
     */
    fun observeForever(owner: LifecycleOwner, observer: Observer<Any?>)
}