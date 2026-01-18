package com.melodiousplayer.android.adapter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateTypeAdapter : JsonSerializer<Date>, JsonDeserializer<Date> {

    override fun serialize(
        src: Date?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        // 序列化时返回时间戳
        return JsonPrimitive(src?.time)
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Date? {
        // 如果是数字，就当作时间戳处理，如果是字符串，尝试按标准格式解析
        if (json?.isJsonPrimitive == true && json.asJsonPrimitive.isNumber) {
            return Date(json.asLong)
        } else if (json?.isJsonPrimitive == true && json.asJsonPrimitive.isString) {
            val dateString = json.asString
            return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).parse(dateString)
        }
        return null
    }

}