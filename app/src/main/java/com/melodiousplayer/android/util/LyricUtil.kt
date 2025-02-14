package com.melodiousplayer.android.util

import com.melodiousplayer.android.model.LyricBean
import java.io.File

/**
 * 歌词解析util
 */
class LyricUtil {

    /**
     * 解析歌词文件获取歌词集合
     */
    fun parseLyric(file: File): List<LyricBean> {
        // 创建集合
        val list = ArrayList<LyricBean>()
        // 判断歌词是否为空
        if (!file.exists()) {
            list.add(LyricBean(0, "歌词加载错误"))
            return list
        }
        // 解析歌词文件，添加到集合中
        // 读取歌词文件，返回每一行数据集合
        val linesList = file.readLines()
        for (line in linesList) {
            // 解析一行
            val lineList: List<LyricBean> = parseLine(line)
            // 添加到集合中
        }
        // 返回集合
        return list
    }

}