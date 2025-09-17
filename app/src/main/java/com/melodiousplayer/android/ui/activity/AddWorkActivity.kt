package com.melodiousplayer.android.ui.activity

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 添加作品界面
 */
class AddWorkActivity : BaseActivity(), ToolBarManager, View.OnClickListener {

    private lateinit var addMusic: Button
    private lateinit var addMV: Button
    private lateinit var addList: Button

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_work
    }

    override fun initData() {
        initAddWorkToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        addMusic = findViewById(R.id.addMusic)
        addMV = findViewById(R.id.addMV)
        addList = findViewById(R.id.addList)
    }

    override fun initListener() {
        addMusic.setOnClickListener(this)
        addMV.setOnClickListener(this)
        addList.setOnClickListener(this)
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
            R.id.addMusic -> {
                // 进入添加音乐界面
                startActivity(Intent(this, AddMusicActivity::class.java))
            }

            R.id.addMV -> {
                myToast("添加MV")
            }

            R.id.addList -> {
                myToast("添加悦单")
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}