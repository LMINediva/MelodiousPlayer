package com.melodiousplayer.android.net

import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink

/**
 * 一个继承自RequestBody的类，用于监听上传进度
 */
class ProgressRequestBody(
    private val delegate: RequestBody,
    private val listener: ProgressListener
) : RequestBody() {

    override fun contentType(): MediaType? = delegate.contentType()

    override fun contentLength(): Long = delegate.contentLength()

    override fun writeTo(sink: BufferedSink) {
    }

}