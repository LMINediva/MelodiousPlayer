package com.melodiousplayer.android.util

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * 应用外的本地视频请求，真实播放路径获取工具类，
 * 适用于Android 7.0 之后SD卡文件访问问题
 */
object FileUtil {

    /**
     * 解析网络视频的URL，获取文件名
     */
    fun getFileNameFromUrl(url: String): String {
        val fileName = url.substringAfterLast('/')
        return fileName
    }

    /**
     * 通过应用外的本地视频请求提供的URI，获取本地视频的真实路径
     */
    fun getFileFromUri(uri: Uri?, context: Context?): File? {
        return if (uri == null) {
            null
        } else when (uri.scheme) {
            "content" -> getFileFromContentUri(uri, context)
            "file" -> File(uri.path)
            else -> null
        }
    }

    /**
     * 从给定的“content://URL”中获取文件的对应路径
     */
    private fun getFileFromContentUri(
        contentUri: Uri?,
        context: Context?
    ): File? {
        if (contentUri == null) {
            return null
        }
        var file: File? = null
        var filePath: String? = null
        val fileName: String
        val filePathColumn =
            arrayOf(
                MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.DISPLAY_NAME
            )
        val contentResolver: ContentResolver? = context?.contentResolver
        val cursor: Cursor? = contentResolver?.query(
            contentUri, filePathColumn, null, null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            try {
                filePath = cursor.getString(cursor.getColumnIndexOrThrow(filePathColumn[0]))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            fileName = cursor.getString(cursor.getColumnIndexOrThrow(filePathColumn[1]))
            cursor.close()
            if (!TextUtils.isEmpty(filePath)) {
                file = File(filePath)
            }
            if (!file!!.exists() || file.length() <= 0 || TextUtils.isEmpty(filePath)) {
                filePath = getPathFromInputStreamUri(context, contentUri, fileName)
                file = File(filePath)
            }
            if (!TextUtils.isEmpty(filePath)) {
                file = File(filePath)
            }
        }
        return file
    }

    /**
     * 用流拷贝一份文件到自己的APP目录下
     */
    fun getPathFromInputStreamUri(
        context: Context?,
        uri: Uri,
        fileName: String
    ): String? {
        var inputStream: InputStream? = null
        var filePath: String? = null
        if (uri.authority != null) {
            try {
                inputStream = context?.contentResolver?.openInputStream(uri)
                val file = createTemporalFileFrom(context, inputStream, fileName)
                filePath = file!!.path
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close()
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }
        return filePath
    }

    /**
     * 创建临时的视频文件
     */
    @Throws(IOException::class)
    fun createTemporalFileFrom(
        context: Context?,
        inputStream: InputStream?,
        fileName: String
    ): File? {
        var targetFile: File? = null
        if (inputStream != null) {
            var read: Int
            // 缓存大小为8KB
            val buffer = ByteArray(8 * 1024)
            // 自己定义拷贝文件路径，此处为放置在应用的缓存目录下
            targetFile = File(context?.cacheDir, fileName)
            if (targetFile.exists()) {
                targetFile.delete()
            }
            val outputStream: OutputStream = FileOutputStream(targetFile)
            while (inputStream.read(buffer).also { read = it } != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return targetFile
    }

}