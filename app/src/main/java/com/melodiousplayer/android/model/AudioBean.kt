package com.melodiousplayer.android.model

import android.database.Cursor
import android.provider.MediaStore.Audio.Media

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
                audioBean.data = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
                audioBean.size = cursor.getLong(cursor.getColumnIndexOrThrow(Media.SIZE))
                audioBean.displayName =
                    cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME))
                audioBean.displayName =
                    audioBean.displayName.substring(0, audioBean.displayName.lastIndexOf("."))
                audioBean.artist = cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST))
            }
            return audioBean
        }
    }

}