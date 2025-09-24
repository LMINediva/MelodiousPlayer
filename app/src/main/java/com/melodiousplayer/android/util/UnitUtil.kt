package com.melodiousplayer.android.util

import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 单位转换工具类
 */
object UnitUtil {

    /**
     * 将字节转换为KB或MB，精确到小数点后两位
     */
    fun bytesToSize(bytes: Long): String {
        val KB = 1024.0
        val MB = KB * 1024.0

        val resultInKB = bytes / KB
        val resultInMB = bytes / MB

        return if (resultInMB < 1) {
            // 如果不足1MB，则转换为KB并四舍五入到小数点后两位
            val kbValue = BigDecimal(resultInKB).setScale(2, RoundingMode.HALF_UP)
            "$kbValue KB"
        } else {
            // 否则，直接四舍五入到小数点后两位
            val mbValue = BigDecimal(resultInMB).setScale(2, RoundingMode.HALF_UP)
            "$mbValue MB"
        }
    }

}