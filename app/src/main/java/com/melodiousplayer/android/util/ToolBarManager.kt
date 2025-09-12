package com.melodiousplayer.android.util

import android.widget.TextView
import androidx.appcompat.widget.Toolbar

/**
 * 所有界面toolbar的管理类
 */
interface ToolBarManager {

    val toolbar: Toolbar
    val toolbarTitle: TextView

    /**
     * 初始化主界面中的toolbar
     */
    fun initMainToolBar() {
        // 设置标题
        toolbarTitle.text = "悦听影音"
    }

    /**
     * 处理设置界面的toolbar
     */
    fun initSettingToolbar() {
        toolbarTitle.text = "设置界面"
    }

    /**
     * 初始化个人信息界面中的toolbar
     */
    fun initUserInfoToolBar() {
        // 设置标题
        toolbarTitle.text = "个人信息"
    }

    /**
     * 初始化修改用户登录密码界面中的toolbar
     */
    fun initChangePasswordToolBar() {
        // 设置标题
        toolbarTitle.text = "修改密码"
    }

}