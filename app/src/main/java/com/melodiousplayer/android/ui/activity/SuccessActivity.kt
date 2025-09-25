package com.melodiousplayer.android.ui.activity

import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 操作成功界面
 */
class SuccessActivity : BaseActivity(), ToolBarManager, View.OnClickListener {

    private lateinit var backButton: Button

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_success
    }

    override fun initData() {
        initSuccessToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        backButton = findViewById(R.id.backButton)
    }

    override fun initListener() {
        backButton.setOnClickListener(this)
    }

    /**
     * Toolbar上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // 跳转到添加作品界面
                startActivityAndFinish<AddWorkActivity>()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backButton -> {
                // 跳转到添加作品界面
                startActivityAndFinish<AddWorkActivity>()
            }
        }
    }

    override fun onBackPressed() {
        // 跳转到添加作品界面
        startActivityAndFinish<AddWorkActivity>()
        super.onBackPressed()
    }

}