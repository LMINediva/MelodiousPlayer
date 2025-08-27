package com.melodiousplayer.android.net

import com.melodiousplayer.android.util.ThreadUtil
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

/**
 * 发送网络请求类
 */
class NetManager private constructor() {

    val client by lazy { OkHttpClient() }

    // 单例实现
    companion object {
        val manager by lazy { NetManager() }
    }

    /**
     * 发送GET网络请求
     */
    fun <RESPONSE> sendRequest(req: MRequest<RESPONSE>, token: String) {
        val request = Request.Builder()
            .url(req.url)
            .get()
            .addHeader("token", token)
            .build()
        client.newCall(request).enqueue(object : Callback {
            /**
             * 子线程中调用
             */
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        // 回调到view层处理
                        req.handler.onError(req.type, e.message)
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
                        req.handler.onSuccess(req.type, parseResult)
                    }
                })
            }
        })
    }

    /**
     * 发送POST网络请求
     */
    fun <RESPONSE> sendPostRequest(
        req: MRequest<RESPONSE>,
        params: Pair<String, Any>?,
        token: String
    ) {
        var requestBody: RequestBody? = null
        if (params !== null) {
            val builder = FormBody.Builder()
            builder.add(params.first, params.second.toString())
            requestBody = builder.build()
        } else {
            requestBody = FormBody.Builder().build()
        }
        val request = Request.Builder()
            .url(req.url)
            .post(requestBody)
            .addHeader("token", token)
            .build()
        client.newCall(request).enqueue(object : Callback {
            /**
             * 子线程中调用
             */
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtil.runOnMainThread { // 回调到view层处理
                    req.handler.onError(req.type, e.message)
                }
            }

            /**
             * 子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val parseResult = req.parseResult(result)
                ThreadUtil.runOnMainThread { req.handler.onSuccess(req.type, parseResult) }
            }
        })
    }

    /**
     * 发送POST带JSON参数的网络请求
     */
    fun <RESPONSE> sendPostWithJSONRequest(req: MRequest<RESPONSE>, json: String) {
        var requestBody: RequestBody =
            RequestBody.create("application/json;charset=utf-8".toMediaType(), json)
        val request = Request.Builder()
            .url(req.url)
            .post(requestBody)
            .build()
        client.newCall(request).enqueue(object : Callback {
            /**
             * 子线程中调用
             */
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtil.runOnMainThread { // 回调到view层处理
                    req.handler.onError(req.type, e.message)
                }
            }

            /**
             * 子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val parseResult = req.parseResult(result)
                ThreadUtil.runOnMainThread { req.handler.onSuccess(req.type, parseResult) }
            }
        })
    }

}