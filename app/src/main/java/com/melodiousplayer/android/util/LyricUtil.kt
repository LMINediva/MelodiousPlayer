package com.melodiousplayer.android.util

import com.melodiousplayer.android.model.LyricBean
import java.io.File
import java.util.regex.Pattern

/**
 * 歌词解析工具类
 */
object LyricUtil {

    /**
     * 解析歌词文件获取歌词集合
     */
    fun parseLyric(file: File): List<LyricBean> {
        // 创建集合
        val list = ArrayList<LyricBean>()
        // 判断歌词是否为空
        if (!file.exists()) {
            list.add(LyricBean(0, "歌词未找到"))
            return list
        }
        // 解析歌词文件，添加到集合中
        // 读取歌词文件，返回每一行数据集合
        val linesList = file.readLines()
        for (line in linesList) {
            // 解析一行
            val lineList: List<LyricBean> = parseLine(line)
            // 添加到集合中
            list.addAll(lineList)
        }
        // 歌词排序
        list.sortBy { it.startTime }
        // 返回集合
        return list
    }

    /**
     * 解析在线歌词文件获取歌词集合
     */
    fun parseOnlineLyric(lyric: String?): List<LyricBean> {
        // 创建集合
        val list = ArrayList<LyricBean>()
        // 判断歌词是否为空
        if (lyric == null) {
            list.add(LyricBean(0, "歌词未找到"))
            return list
        }
        // 解析歌词文件，添加到集合中
        // 读取歌词文件，返回每一行数据集合
        val linesList = lyric.lines()
        for (line in linesList) {
            // 解析一行
            val lineList: List<LyricBean> = parseLine(line)
            // 添加到集合中
            list.addAll(lineList)
        }
        // 歌词排序
        list.sortBy { it.startTime }
        // 返回集合
        return list
    }

    /**
     * 解析一行歌词
     * [01:33.67 [02:46.87 伤心的泪儿谁来擦
     */
    private fun parseLine(line: String): List<LyricBean> {
        // 创建集合
        val list = ArrayList<LyricBean>()
        // 解析当前行
        val arr = line.split("]")
        // 获取歌词内容
        val content = arr.get(arr.size - 1)
        for (index in 0 until arr.size - 1) {
            val value = arr.get(index)
            // 将非时间内容进行过滤处理
            val pattern = Pattern.compile("^\\[(\\d{2}):(\\d{2})(.*)")
            if (!pattern.matcher(value).matches()) {
                list.add(LyricBean(0, content))
                continue
            }
            val startTime: Int = parseTime(value)
            list.add(LyricBean(startTime, content))
        }
        // 返回集合
        return list
    }

    /**
     * 解析时间
     * 01 01 33.67
     */
    private fun parseTime(get: String): Int {
        // 去掉“[”
        val timeS = get.substring(1)
        // 按照“:”切割
        val list = timeS.split(":")
        var hour = 0
        var min = 0
        var sec = 0f
        if (list.size == 3) {
            // 有小时
            hour = (list[0].toInt()) * 60 * 60 * 1000
            min = (list[1].toInt()) * 60 * 1000
            sec = (list[2].toFloat()) * 1000
        } else {
            // 没有小时
            min = (list[0].toInt()) * 60 * 1000
            sec = (list[1].toFloat()) * 1000
        }
        return (hour + min + sec).toInt()
    }

}