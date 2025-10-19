package com.melodiousplayer.android.ui.activity

import android.content.Context
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.UnitUtil


/**
 * 清除缓存界面
 */
class ClearCacheActivity : BaseActivity(), ToolBarManager, View.OnClickListener {

    private lateinit var cacheSizeText: TextView
    private lateinit var clearCache: Button

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_clear_cache
    }

    override fun initData() {
        initClearCacheToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        cacheSizeText = findViewById(R.id.cacheSize)
        clearCache = findViewById(R.id.clearCache)
        val cacheSize = getAppCacheSize(this)
        cacheSizeText.text = cacheSize
    }

    override fun initListener() {
        clearCache.setOnClickListener(this)
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
            R.id.clearCache -> {
                clearAppCache(this)
                cacheSizeText.text = "0B"
            }
        }
    }

    /**
     * 获取应用缓存大小
     */
    fun getAppCacheSize(context: Context): String {
        var cacheSize = 0L
        val dir = context.cacheDir
        val dirList = dir.listFiles()
        if (dirList != null) {
            for (file in dirList) {
                cacheSize += file.length()
            }
        }
        return UnitUtil.convertToDisplaySize(cacheSize)
    }

    /**
     * 清除应用缓存大小
     */
    fun clearAppCache(context: Context) {
        val dir = context.cacheDir
        val files = dir.listFiles() ?: return
        for (file in files) {
            file.delete()
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}