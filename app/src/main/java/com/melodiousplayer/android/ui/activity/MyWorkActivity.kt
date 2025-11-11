package com.melodiousplayer.android.ui.activity

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.MyWorkPagerAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.util.ToolBarManager

/**
 * 我的作品界面
 */
class MyWorkActivity : BaseActivity(), ToolBarManager {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var currentUser: UserBean
    private lateinit var token: String
    private lateinit var adapter: MyWorkPagerAdapter

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_work
    }

    override fun initData() {
        initMyWorkToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        tabLayout = findViewById(R.id.tabLayout)
        viewPager = findViewById(R.id.viewPager)
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
            currentUser.loginDate = null
            currentUser.createTime = null
            currentUser.updateTime = null
        }
        // 从SharedPreferences文件中读取token的值
        token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "").toString()
        adapter = MyWorkPagerAdapter(
            this, supportFragmentManager,
            currentUser, token
        )
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> if (resultCode == RESULT_OK) {
                val refresh = data?.getBooleanExtra("refresh", false)
                if (refresh == true) {
                    val userSerialized = data.getSerializableExtra("user")
                    if (userSerialized != null) {
                        currentUser = userSerialized as UserBean
                        currentUser.loginDate = null
                        currentUser.createTime = null
                        currentUser.updateTime = null
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}