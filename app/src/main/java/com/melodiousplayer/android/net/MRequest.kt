package com.melodiousplayer.android.net

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.melodiousplayer.android.model.HomeItemBean
import java.lang.reflect.ParameterizedType

/**
 * 所有请求基类
 */
class MRequest<RESPONSE>(val url: String, val handler: ResponseHandler<RESPONSE>) {

    /**
     * 解析网络请求结果
     */
    fun parseResult(result: String?): RESPONSE {
        val gson = Gson()
        // 获取泛型类型
        val type = (this.javaClass
            .genericSuperclass as ParameterizedType).actualTypeArguments[0]
        val list = gson.fromJson<RESPONSE>(result, type)
        return list
    }

}