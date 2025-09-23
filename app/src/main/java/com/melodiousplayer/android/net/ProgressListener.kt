package com.melodiousplayer.android.net

/**
 * 文件上传进度监听接口
 */
interface ProgressListener {

    fun onProgress(bytesWritten: Long, contentLength: Long, done: Boolean = false)

}