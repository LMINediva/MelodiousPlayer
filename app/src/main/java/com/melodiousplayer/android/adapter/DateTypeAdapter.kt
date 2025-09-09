package com.melodiousplayer.android.adapter

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateTypeAdapter : TypeAdapter<Date>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)

    @Throws(IOException::class)
    override fun write(out: JsonWriter?, value: Date?) {
        if (value == null) {
            out?.value("")
        } else {
            out?.value(dateFormat.format(value))
        }
    }

    @Throws(IOException::class)
    override fun read(`in`: JsonReader?): Date? {
        if (`in`?.peek() == JsonToken.NULL) {
            `in`.nextNull()
            return null
        } else {
            return try {
                val date = `in`?.nextString()
                dateFormat.parse(date)
            } catch (e: ParseException) {
                null
            }
        }
    }

}