package com.melodiousplayer.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * 提供一种全局获取Context的方式
 */
class MelodiousPlayerApplication : Application() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }

}