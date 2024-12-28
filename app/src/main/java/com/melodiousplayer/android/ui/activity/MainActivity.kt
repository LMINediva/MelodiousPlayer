package com.melodiousplayer.android.ui.activity

import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.util.FragmentUtil
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
        // 将首页添加到fragment中
        val homeFragment = FragmentUtil.fragmentUtil.getFragment(R.id.tab_home)
        if (homeFragment != null) {
            replaceFragment(homeFragment, R.id.tab_home.toString())
        }
    }

    override fun initListener() {
        // 设置tab切换监听
        bottomBar.setOnItemSelectedListener { item ->
            // item.itemId代表tabId参数
            val fragment = FragmentUtil.fragmentUtil.getFragment(item.itemId)
            if (fragment != null) {
                replaceFragment(fragment, item.itemId.toString())
            }
            true
        }
    }

    /**
     * 替换fragment函数
     */
    private fun replaceFragment(fragment: BaseFragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment, tag)
        transaction.commit()
    }

}