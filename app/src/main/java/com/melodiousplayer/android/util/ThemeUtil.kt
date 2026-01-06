package com.melodiousplayer.android.util

import android.content.Context
import android.content.res.Configuration

/**
 * 主题相关的工具类
 */
object ThemeUtil {

    fun isDarkTheme(context: Context): Boolean {
        val flag = context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        return flag == Configuration.UI_MODE_NIGHT_YES
    }

}