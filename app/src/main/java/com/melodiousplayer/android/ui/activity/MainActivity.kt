package com.melodiousplayer.android.ui.activity

import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 主界面
 */
class MainActivity : BaseActivity(), ToolBarManager {

    private lateinit var bottomBar: BottomNavigationView

    // 惰性加载
    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        initMainToolBar()
        bottomBar = findViewById(R.id.bottomBar)
    }

    override fun initListener() {
        // 设置tab切换监听
        bottomBar.setOnItemSelectedListener {

            true
        }
    }

}