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

    private lateinit var backHomePageButton: Button

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_success
    }

    override fun initData() {
        initSuccessToolBar(this)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        backHomePageButton = findViewById(R.id.backHomePageButton)
        val isMyMusic = intent.getBooleanExtra("isMyMusic", false)
        val isMyMV = intent.getBooleanExtra("isMyMV", false)
        val isMyMusicList = intent.getBooleanExtra("isMyMusicList", false)
        if (isMyMusic || isMyMV || isMyMusicList) {
            backHomePageButton.text = "返回"
        }
    }

    override fun initListener() {
        backHomePageButton.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.backHomePageButton -> {
                finish()
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}