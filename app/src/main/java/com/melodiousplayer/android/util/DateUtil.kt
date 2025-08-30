package com.melodiousplayer.android.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 日期工具类
 */
object DateUtil {

    /**
     * 将Date类型日期转换为字符串
     */
    fun formatDateToString(date: Date): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

}