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
     * 初始化设置界面的toolbar
     */
    fun initSettingToolbar() {
        toolbarTitle.text = "设置"
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
     * 初始化修改音乐界面中的toolbar
     */
    fun initEditMusicToolBar() {
        // 设置标题
        toolbarTitle.text = "修改音乐"
    }

    /**
     * 初始化添加MV界面中的toolbar
     */
    fun initAddMVToolBar() {
        // 设置标题
        toolbarTitle.text = "添加MV"
    }

    /**
     * 初始化编辑MV界面中的toolbar
     */
    fun initEditMVToolBar() {
        // 设置标题
        toolbarTitle.text = "编辑MV"
    }

    /**
     * 初始化添加音乐清单界面中的toolbar
     */
    fun initAddMusicListToolBar() {
        // 设置标题
        toolbarTitle.text = "添加悦单"
    }

    /**
     * 初始化编辑音乐清单界面中的toolbar
     */
    fun initEditMusicListToolBar() {
        // 设置标题
        toolbarTitle.text = "编辑悦单"
    }

    /**
     * 初始化成功界面中的toolbar
     */
    fun initSuccessToolBar() {
        // 设置标题
        toolbarTitle.text = "操作结果"
    }

    /**
     * 初始化我的作品界面中的toolbar
     */
    fun initMyWorkToolBar() {
        // 设置标题
        toolbarTitle.text = "我的作品"
    }

    /**
     * 初始化视频播放界面中的toolbar
     */
    fun initVideoPlayerToolBar() {
        // 设置标题
        toolbarTitle.text = "视频详情"
    }

    /**
     * 初始化关于界面中的toolbar
     */
    fun initAboutToolBar() {
        // 设置标题
        toolbarTitle.text = "关于"
    }

    /**
     * 初始化清理缓存界面中的toolbar
     */
    fun initClearCacheToolBar() {
        // 设置标题
        toolbarTitle.text = "清理缓存"
    }

    /**
     * 初始化用户反馈界面中的toolbar
     */
    fun initFeedBackToolBar() {
        // 设置标题
        toolbarTitle.text = "用户反馈"
    }

    /**
     * 初始化通知设置界面中的toolbar
     */
    fun initNotificationSettingToolBar() {
        // 设置标题
        toolbarTitle.text = "通知设置"
    }

}