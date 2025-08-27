package com.melodiousplayer.android.ui.activity

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.base.InputDialogListener
import com.melodiousplayer.android.base.MessageListener
import com.melodiousplayer.android.base.OnDataChangedListener
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.TokenLoginRequest
import com.melodiousplayer.android.ui.fragment.HomeFragment
import com.melodiousplayer.android.ui.fragment.InputDialogFragment
import com.melodiousplayer.android.util.FragmentUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import de.hdodenhof.circleimageview.CircleImageView

/**
 * 主界面
 */
class MainActivity : BaseActivity(), ToolBarManager, InputDialogListener, MessageListener,
    OnDataChangedListener, View.OnClickListener, ResponseHandler<UserResultBean> {

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toLogin: TextView
    private lateinit var usernameText: TextView
    private lateinit var avatarImage: CircleImageView
    private lateinit var currentUser: UserBean

    // 惰性加载
    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        initMainToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        bottomBar = findViewById(R.id.bottomBar)
        drawerLayout = findViewById(R.id.drawerLayout)
        navView = findViewById(R.id.navView)
        val headerLayout = navView.inflateHeaderView(R.layout.nav_header)
        toLogin = headerLayout.findViewById(R.id.toLogin)
        usernameText = headerLayout.findViewById(R.id.usernameText)
        avatarImage = headerLayout.findViewById(R.id.avatarImage)
        // 将首页添加到fragment中
        val homeFragment = FragmentUtil.fragmentUtil.getFragment(R.id.tab_home)
        if (homeFragment != null) {
            replaceFragment(homeFragment, R.id.tab_home.toString())
        }
        toLogin.setOnClickListener(this)
        // 从SharedPreferences文件中读取token的值
        val token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "")
        if (!token.isNullOrEmpty()) {
            TokenLoginRequest(this).execute(token)
        }
        // 登录成功显示用户名和头像
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            val user = userSerialized as UserBean
            usernameText.text = user.username
            usernameText.visibility = View.VISIBLE
            toLogin.visibility = View.GONE
            Glide.with(this).load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.userAvatarPath + user.avatar
            ).into(avatarImage)
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 设置action按钮
        menuInflater.inflate(R.menu.main, menu)
        return true
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
        // 设置侧边菜单栏项的点击事件
        navView.setNavigationItemSelectedListener {
            drawerLayout.closeDrawers()
            true
        }
    }

    /**
     * 侧边栏头部控件的点击事件
     */
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.toLogin -> {
                startActivityAndFinish<LoginActivity>()
            }
        }
    }

    /**
     * 自定义标题栏上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }

            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    /**
     * 替换fragment函数
     */
    private fun replaceFragment(fragment: BaseFragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment, tag)
        transaction.commit()
    }

    private fun showInputDialog() {
        val dialog = InputDialogFragment()
        dialog.show(supportFragmentManager, "InputDialog")
    }

    override fun onFinishEdit(inputText: String) {
        URLProviderUtils.serverAddress = inputText
        Log.i("MainActivity", "IP: " + URLProviderUtils.serverAddress)
        refreshData()
    }

    override fun onMessageReceived(message: String) {
        if (message == "error") {
            showInputDialog()
        }
    }

    override fun onDataChanged() {
        val fragment = FragmentUtil.fragmentUtil.getFragment(R.id.tab_home) as HomeFragment
        fragment.onDataChanged()
    }

    fun refreshData() {
        onDataChanged()
    }

    override fun onError(type: Int, msg: String?) {
        myToast(getString(R.string.network_error))
    }

    override fun onSuccess(type: Int, result: UserResultBean) {
        when (result.code) {
            4000 -> myToast(getString(R.string.token_null_error))
            4001 -> myToast(getString(R.string.token_expire_error))
            4002 -> myToast(getString(R.string.token_fail_error))
        }
        if (result.currentUser !== null) {
            myToast(getString(R.string.login_success))
            currentUser = result.currentUser!!
            usernameText.text = currentUser.username
            usernameText.visibility = View.VISIBLE
            toLogin.visibility = View.GONE
            Glide.with(this).load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.userAvatarPath + currentUser.avatar
            ).into(avatarImage)
        }
    }

}