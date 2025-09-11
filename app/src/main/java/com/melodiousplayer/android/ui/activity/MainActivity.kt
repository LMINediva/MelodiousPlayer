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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.base.InputDialogListener
import com.melodiousplayer.android.base.MessageListener
import com.melodiousplayer.android.base.OnDataChangedListener
import com.melodiousplayer.android.contract.LogoutContract
import com.melodiousplayer.android.contract.TokenLoginContract
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.presenter.impl.LogoutPresenterImpl
import com.melodiousplayer.android.presenter.impl.TokenLoginPresenterImpl
import com.melodiousplayer.android.ui.fragment.HomeFragment
import com.melodiousplayer.android.ui.fragment.InputDialogFragment
import com.melodiousplayer.android.util.FragmentUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.io.Serializable

/**
 * 主界面
 */
class MainActivity : BaseActivity(), ToolBarManager, InputDialogListener, MessageListener,
    OnDataChangedListener, View.OnClickListener, TokenLoginContract.View, LogoutContract.View {

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toLogin: TextView
    private lateinit var usernameText: TextView
    private lateinit var avatarImage: CircleImageView
    private lateinit var currentUser: UserBean
    private lateinit var menu: Menu
    private val UPDATE_REQUEST = 1
    private var userSerialized: Serializable? = null
    private val presenter = TokenLoginPresenterImpl(this)
    private val logoutPresenter = LogoutPresenterImpl(this)

    // 惰性加载
    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        initMainToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
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
            presenter.tokenLogin(token)
        }
        // 登录成功显示用户名和头像
        menu = navView.menu
        userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
            usernameText.text = currentUser.username
            usernameText.visibility = View.VISIBLE
            toLogin.visibility = View.GONE
            Glide.with(this)
                .load(
                    URLProviderUtils.protocol + URLProviderUtils.serverAddress
                            + URLProviderUtils.userAvatarPath + currentUser.avatar
                )
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(avatarImage)
            // 显示侧边栏所有菜单项
            for (i in 0 until menu.size()) {
                menu.getItem(i).isVisible = true
            }
            drawerLayout.openDrawer(GravityCompat.START)
        } else {
            // 隐藏侧边栏所有菜单项
            for (i in 0 until menu.size()) {
                menu.getItem(i).isVisible = false
            }
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
        navView.setNavigationItemSelectedListener { menuItem ->
            // 处理菜单项点击事件
            handleMenuItemClick(menuItem.itemId)
            drawerLayout.closeDrawers()
            true
        }
    }

    private fun handleMenuItemClick(itemId: Int) {
        when (itemId) {
            R.id.navUserInfo -> {
                // 进入用户个人信息界面，传递用户信息
                val intent = Intent(this, UserInfoActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivityForResult(intent, UPDATE_REQUEST)
            }

            R.id.navChangePassword -> {

            }

            R.id.navMyWorks -> {

            }

            R.id.navLogout -> {
                logoutPresenter.logout()
            }
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

    override fun onTokenLoginSuccess(userResult: UserResultBean?) {
        myToast(getString(R.string.login_success))
        currentUser = userResult?.currentUser!!
        usernameText.text = currentUser.username
        usernameText.visibility = View.VISIBLE
        toLogin.visibility = View.GONE
        Glide.with(this).load(
            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                    + URLProviderUtils.userAvatarPath + currentUser.avatar
        ).into(avatarImage)
        // 显示侧边栏所有菜单项
        for (i in 0 until menu.size()) {
            menu.getItem(i).isVisible = true
        }
    }

    override fun onTokenLoginFailed(msg: String?) {
        // 隐藏侧边栏所有菜单项
        for (i in 0 until menu.size()) {
            menu.getItem(i).isVisible = false
        }
        msg?.let { myToast(it) }
    }

    override fun onLogoutSuccess(msg: String?) {
        msg?.let { myToast(it) }
        currentUser = UserBean(
            null, null, null,
            null, null, null, null,
            null, null, null, null, null
        )
        userSerialized = null
        // 在SharedPreferences文件中删除token的值
        getSharedPreferences("data", Context.MODE_PRIVATE)
            .edit().remove("token").apply()
        usernameText.text = ""
        usernameText.visibility = View.GONE
        toLogin.visibility = View.VISIBLE
        Glide.with(this).load(R.drawable.nav_icon).into(avatarImage)
        // 隐藏侧边栏所有菜单项
        for (i in 0 until menu.size()) {
            menu.getItem(i).isVisible = false
        }
    }

    override fun onLogoutFailed(msg: String?) {
        msg?.let { myToast(it) }
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UPDATE_REQUEST -> if (resultCode == RESULT_OK) {
                val newAvatar = data?.getStringExtra("newAvatar")
                val newUsername = data?.getStringExtra("newUsername")
                if (!newAvatar.isNullOrEmpty()) {
                    currentUser.avatar = newAvatar
                    Glide.with(this)
                        .load(
                            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                                    + URLProviderUtils.userAvatarPath + newAvatar
                        )
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(avatarImage)
                }
                if (!newUsername.isNullOrEmpty()) {
                    currentUser.username = newUsername
                    usernameText.text = newUsername
                }
            }
        }
    }

}