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

    /**
     * 初始化添加作品界面中的toolbar
     */
    fun initAddWorkToolBar() {
        // 设置标题
        toolbarTitle.text = "添加作品"
    }

    /**
     * 初始化添加音乐界面中的toolbar
     */
    fun initAddMusicToolBar() {
        // 设置标题
        toolbarTitle.text = "添加音乐"
    }

    /**
     * 初始化添加MV界面中的toolbar
     */
    fun initAddMVToolBar() {
        // 设置标题
        toolbarTitle.text = "添加MV"
    }

    /**
     * 初始化成功界面中的toolbar
     */
    fun initSuccessToolBar() {
        // 设置标题
        toolbarTitle.text = "操作结果"
    }

}