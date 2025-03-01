package com.melodiousplayer.android.util

/**
 * 字符串处理工具类
 */
object StringUtil {

    val HOUR = 60 * 60 * 1000
    val MIN = 60 * 1000
    val SEC = 1000

    /**
     * 将音乐文件的时长解析为字符串格式
     */
    fun parseDuration(progress: Int): String {
        val hour = progress / HOUR
        val min = progress % HOUR / MIN
        val sec = progress % MIN / SEC
        var result: String = ""
        if (hour == 0) {
            // 不足1小时，不显示小时
            result = String.format("%02d:%02d", min, sec)
        } else {
            result = String.format("%02d:%02d:%02d", hour, min, sec)
        }
        return result
    }

}