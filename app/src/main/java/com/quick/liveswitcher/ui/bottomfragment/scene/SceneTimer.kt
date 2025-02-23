package com.quick.liveswitcher.ui.bottomfragment.scene

import android.os.Handler
import android.os.Looper

class SceneTimer {
        private var handler: Handler = Handler(Looper.getMainLooper())
        private var isTimerStarted: Boolean = false

        private val timerRunnable = object : Runnable {
            override fun run() {

                // 任务执行完后，再次调度定时器
                handler.postDelayed(this, 2000) // 1000毫秒（1秒）后再次执行
            }
        }

        fun startTimer() {
            if (!isTimerStarted) {
                isTimerStarted = true
                handler.post(timerRunnable)
            }
        }

        fun stopTimer() {
            if (isTimerStarted) {
                handler.removeCallbacks(timerRunnable)
                isTimerStarted = false
            }
        }

}