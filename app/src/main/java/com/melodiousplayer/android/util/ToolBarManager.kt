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
        // 设置标题
        toolbar.setTitle("悦听影音")
    }

    /**
     * 处理设置界面的toolbar
     */
    fun initSettingToolbar() {
        toolbar.setTitle("设置界面")
    }

}