package com.melodiousplayer.android.util

import android.database.Cursor

/**
 * 打印Cursor中的数据的工具类
 */
object CursorUtil {

    fun logCursor(cursor: Cursor?) {
        cursor?.let {
            // 将cursor游标复位
            it.moveToPosition(-1)
            while (it.moveToNext()) {
                for (index in 0 until it.columnCount) {
                    println("key=${it.getColumnName(index)} --value=${it.getString(index)}")
                }
            }
        }
    }

}