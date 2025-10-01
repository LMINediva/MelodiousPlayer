package com.melodiousplayer.android.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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

    /**
     * 将字符串日期转换为Date类型
     */
    fun formatStringToDate(dateString: String): Date? {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return simpleDateFormat.parse(dateString)
    }

    /**
     * 获取当前时间并转换为字符串
     */
    fun getCurrentTime(): String {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val now = calendar.time
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return formatter.format(now)
    }

}