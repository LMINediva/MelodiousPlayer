package com.melodiousplayer.android.util

import android.os.Handler
import android.os.Looper

/**
 * 线程工具类
 */
object ThreadUtil {

    val handler = Handler(Looper.getMainLooper())

    /**
     * 运行在主线程中
     */
    fun runOnMainThread(runnable: Runnable) {
        handler.post(runnable)
    }

}