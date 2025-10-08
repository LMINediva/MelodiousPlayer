package com.melodiousplayer.android.ui.activity

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 添加作品界面
 */
class AddWorkActivity : BaseActivity(), ToolBarManager, View.OnClickListener {

    private lateinit var addMusic: Button
    private lateinit var addMV: Button
    private lateinit var addList: Button
    private lateinit var currentUser: UserBean

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
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
        }
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
                // 进入添加音乐界面，传递用户信息
                val intent = Intent(this, AddMusicActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivity(intent)
            }

            R.id.addMV -> {
                // 进入添加MV界面，传递用户信息
                val intent = Intent(this, AddMVActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivity(intent)
            }

            R.id.addList -> {
                // 进入添加悦单界面，传递用户信息
                val intent = Intent(this, AddMusicListActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}