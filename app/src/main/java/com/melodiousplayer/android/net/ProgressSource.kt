package com.melodiousplayer.android.net

import okio.Buffer
import okio.ForwardingSource
import okio.Source
import java.io.IOException

/**
 * 包装实际的Source对象，并实现进度回调
 */
class ProgressSource(
    private val source: Source,
    private val listener: ProgressListener,
    private val contentLength: Long
) : ForwardingSource(source) {

    private var totalBytesRead: Long = 0

    @Throws(IOException::class)
    override fun read(sink: Buffer, byteCount: Long): Long {
        val bytesRead = super.read(sink, byteCount)
        // -1表示EOF，即文件结束
        if (bytesRead != -1L) {
            totalBytesRead += bytesRead
            listener.onProgress(totalBytesRead, contentLength)
        }
        return bytesRead
    }

}