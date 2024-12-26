package com.melodiousplayer.android.util

import android.content.Intent
import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.ui.activity.SettingActivity

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
        toolbar.setTitle("悦听视界")
        // 设置action按钮
        toolbar.inflateMenu(R.menu.main)
        // 设置action按钮的点击事件
        // kotlin 和java调用特性
        // 如果java接口中只有一个未实现的方法，可以省略接口对象，直接用{}表示未实现的方法
        toolbar.setOnMenuItemClickListener {
            // 跳转到设置界面
            toolbar.context
                .startActivity(Intent(toolbar.context, SettingActivity::class.java))
            true
        }
    }

    /**
     * 处理设置界面的toolbar
     */
    fun initSettingToolbar() {
        toolbar.setTitle("设置界面")
    }

}