package com.melodiousplayer.android.util

import android.os.Environment
import java.io.File

/**
 * 歌词文件加载util
 */
object LyricLoader {

    // 歌词文件夹
    val dir = File(Environment.getExternalStorageDirectory(), "Download/Lyric")

    /**
     * 根据歌曲名称加载歌词文件
     */
    fun loadLyricFile(displayName: String): File {
        return File(dir, displayName + ".lrc")
    }

}