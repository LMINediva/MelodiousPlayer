package com.melodiousplayer.android.contract

import android.os.Handler
import android.os.Looper

/**
 * 基本协议持久接口
 */
interface BasePresenter {

    companion object {
        val handler by lazy {
            Handler(Looper.getMainLooper())
        }
    }

    fun uiThread(f: () -> Unit) {
        handler.post { f() }
    }

}