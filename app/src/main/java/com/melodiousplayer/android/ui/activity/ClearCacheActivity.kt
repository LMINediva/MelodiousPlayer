package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.UnitUtil
import java.io.File


/**
 * 清除缓存界面
 */
class ClearCacheActivity : BaseActivity(), ToolBarManager, View.OnClickListener {

    private lateinit var cacheSizeText: TextView
    private lateinit var clearCache: Button
    private val PERMISSION_REQUEST = 1

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
        requestPermissions()
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
                myToast("缓存清理成功")
            }
        }
    }

    /**
     * 请求权限
     */
    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, permissions,
                    PERMISSION_REQUEST
                )
            }
        }
    }

    /**
     * 权限请求结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST -> {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        myToast("你拒绝了权限！")
                        break
                    }
                }
            }
        }
    }

    /**
     * 获取应用缓存大小
     */
    private fun getAppCacheSize(context: Context): String {
        var cacheSize: Long = 0
        val cacheDir = context.cacheDir
        val filesDir = context.filesDir
        val externalCacheDir = context.externalCacheDir
        // 获取cache目录的大小
        cacheSize += getFolderSize(cacheDir)
        // 获取files目录的大小
        cacheSize += getFolderSize(filesDir)
        // 获取external cache目录的大小
        if (externalCacheDir != null) {
            cacheSize += getFolderSize(externalCacheDir)
        }
        return UnitUtil.convertToDisplaySize(cacheSize)
    }

    /**
     * 获取目录下文件的大小
     */
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val entries = file.listFiles() ?: return 0
            for (entry in entries) {
                size += if (entry.isDirectory) {
                    getFolderSize(entry)
                } else {
                    entry.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 清除应用缓存大小
     */
    private fun clearAppCache(context: Context) {
        val cacheDir = context.cacheDir
        deleteDir(cacheDir)
        val filesDir = context.filesDir
        deleteDir(filesDir)
        val externalCacheDir = context.externalCacheDir
        if (externalCacheDir != null) {
            deleteDir(externalCacheDir)
        }
    }

    /**
     * 删除目录下的文件
     */
    private fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            for (child in dir.listFiles() ?: return false) {
                deleteDir(child)
            }
        }
        return dir.delete()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}