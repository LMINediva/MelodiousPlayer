package com.melodiousplayer.android.model

import android.database.Cursor

/**
 * 音乐列表条目bean
 */
data class AudioBean(
    var data: String, var size: Long, var displayName: String, var artist: String
) {

    companion object {
        /**
         * 根据特定位置上的cursor获取bean
         */
        fun getAudioBean(cursor: Cursor?): AudioBean {
            // 创建AudioBean对象
            val audioBean = AudioBean("", 0, "", "")
            // 判断Cursor是否为空
            cursor?.let {
                // 解析Cursor并且设置到Bean对象中
            }
            return audioBean
        }
    }

}