package com.melodiousplayer.android.ui.activity

import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 关于界面
 */
class AboutActivity : BaseActivity(), ToolBarManager {

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_about
    }

    override fun initData() {
        initAboutToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
    }

    /**
     * Toolbar上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}