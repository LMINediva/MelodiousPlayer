package com.melodiousplayer.android.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.util.ThreadUtil
import com.melodiousplayer.android.util.URLProviderUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 发送网络请求类
 */
class NetManager private constructor() {

    val client by lazy { OkHttpClient() }

    // 单例实现
    companion object {
        val manager by lazy { NetManager }
    }

    /**
     * 发送网络请求
     */
    fun <RESPONSE> sendRequest(req: MRequest<RESPONSE>) {
        val request = Request.Builder()
            .url(req.url)
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            /**
             * 子线程中调用
             */
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        // 回调到view层处理
                        req.handler.onError(e.message)
                    }
                })
            }

            /**
             * 子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val parseResult = req.parseResult(result)
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        homeView.loadMore(list)
                    }
                })
            }
        })
    }

}