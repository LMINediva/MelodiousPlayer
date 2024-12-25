package com.melodiousplayer.android.util

import androidx.appcompat.widget.Toolbar

/**
 * 所有界面toolbar的管理类
 */
interface ToolBarManager {

    val toolbar: Toolbar

    /**
     * 初始化主界面中的toolbar
     */
    fun initMainToolBar() {
        toolbar.setTitle("悦听视界")
    }

}