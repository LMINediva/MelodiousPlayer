package com.melodiousplayer.android.net

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer

/**
 * 继承自RequestBody的类，用于监听上传进度
 */
class ProgressRequestBody(
    private val requestBody: RequestBody,
    private val listener: ProgressListener
) : RequestBody() {

    override fun contentType(): MediaType? = requestBody.contentType()

    override fun contentLength(): Long = requestBody.contentLength()

    override fun writeTo(sink: BufferedSink) {
        val countingSink = CountingSink(sink).buffer()
        requestBody.writeTo(countingSink)
        countingSink.flush()
    }

    inner class CountingSink(delegate: Sink) : ForwardingSink(delegate) {
        private var bytesWritten = 0L

        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            bytesWritten += byteCount
            listener.onProgress(bytesWritten, contentLength(), bytesWritten == contentLength())
        }
    }

}