package com.melodiousplayer.android.net

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import java.lang.reflect.ParameterizedType
import java.util.Date

/**
 * 所有请求基类
 */
open class MRequest<RESPONSE>(
    val type: Int,
    val url: String,
    val handler: ResponseHandler<RESPONSE>
) {

    /**
     * 解析网络请求结果
     */
    fun parseResult(result: String?): RESPONSE {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        // 获取泛型类型
        val type = (this.javaClass
            .genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val list = gson.fromJson<RESPONSE>(result, type)
        return list
    }

    /**
     * 发送GET网络请求
     */
    fun execute() {
        NetManager.manager.sendRequest(this)
    }

    /**
     * 发送POST网络请求
     */
    fun executePost(params: Pair<String, Any>?) {
        NetManager.manager.sendPostRequest(this, params)
    }

    fun executePostWithJSON(json: String) {
        NetManager.manager.sendPostWithJSONRequest(this, json)
    }

}