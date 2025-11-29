package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.base.OnDataChangedListener
import com.melodiousplayer.android.contract.LogoutContract
import com.melodiousplayer.android.contract.TokenLoginContract
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.presenter.impl.LogoutPresenterImpl
import com.melodiousplayer.android.presenter.impl.TokenLoginPresenterImpl
import com.melodiousplayer.android.ui.fragment.MusicFragment
import com.melodiousplayer.android.util.FragmentUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File
import java.io.Serializable

/**
 * 主界面
 */
class MainActivity : BaseActivity(), ToolBarManager, OnDataChangedListener,
    View.OnClickListener, TokenLoginContract.View, LogoutContract.View {

    private lateinit var bottomBar: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toLogin: TextView
    private lateinit var usernameText: TextView
    private lateinit var avatarImage: CircleImageView
    private lateinit var currentUser: UserBean
    private lateinit var menu: Menu
    private var isLogin: Boolean = false
    private val PERMISSION_REQUEST = 1
    private val UPDATE_AVATAR_REQUEST = 1
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
            isLogin = true
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
        requestPermissions()
        val addOrModifySuccess = intent.getBooleanExtra("addOrModifySuccess", false)
        if (addOrModifySuccess) {
            val fragment = FragmentUtil.fragmentUtil.getFragment(R.id.tab_home) as MusicFragment
            if (fragment.isAdded) {
                fragment.onDataChanged()
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 设置action按钮
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    private fun handleMenuItemClick(itemId: Int) {
        when (itemId) {
            R.id.navUserInfo -> {
                // 进入用户个人信息界面，传递用户信息
                val intent = Intent(this, UserInfoActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivityForResult(intent, UPDATE_AVATAR_REQUEST)
            }

            R.id.navChangePassword -> {
                // 进入修改用户登录密码界面，传递用户信息
                val intent = Intent(this, ChangePasswordActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivity(intent)
            }

            R.id.navAddWork -> {
                // 进入添加作品界面，传递用户信息
                val intent = Intent(this, AddWorkActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivity(intent)
            }

            R.id.navMyWorks -> {
                // 进入我的作品界面，传递用户信息
                val intent = Intent(this, MyWorkActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivity(intent)
            }

            R.id.navLogout -> {
                AlertDialog.Builder(this).apply {
                    setTitle("提示")
                    setMessage("确定要退出登录吗？")
                    setCancelable(false)
                    setPositiveButton("确定") { dialog, which ->
                        logoutPresenter.logout()
                    }
                    setNegativeButton("取消") { dialog, which ->
                    }
                    show()
                }
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
                // 进入设置界面，传递用户信息
                val intent = Intent(this, SettingActivity::class.java)
                if (isLogin) {
                    intent.putExtra("user", currentUser)
                }
                intent.putExtra("isLogin", isLogin)
                startActivity(intent)
            }

            android.R.id.home -> drawerLayout.openDrawer(GravityCompat.START)
        }
        return true
    }

    /**
     * 删除更新APK文件
     */
    private fun deleteAPKFile() {
        // 从SharedPreferences文件中读取apk_file_name的值
        val apkFileName = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("apk_file_name", "")
        if (!apkFileName.isNullOrEmpty()) {
            // 获取externalCacheDir路径
            if (externalCacheDir != null) {
                val apkFile = File(externalCacheDir, apkFileName)
                if (apkFile.exists()) {
                    // 删除已下载的更新APK文件
                    val isDeleted = apkFile.delete()
                    if (isDeleted) {
                        val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
                        editor.remove("apk_file_name")
                        editor.apply()
                        myToast("更新APK文件删除成功")
                    } else {
                        myToast("更新APK文件删除失败")
                    }
                } else {
                    myToast("更新APK文件不存在")
                }
            } else {
                myToast("应用外部缓存存储目录不可用")
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
        var hasAllPermission = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                hasAllPermission = false
                ActivityCompat.requestPermissions(
                    this, permissions,
                    PERMISSION_REQUEST
                )
            }
        }
        if (hasAllPermission) {
            deleteAPKFile()
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
                var allPermissionGranted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        myToast("你拒绝了权限！")
                        allPermissionGranted = false
                        break
                    }
                }
                if (allPermissionGranted) {
                    deleteAPKFile()
                }
            }
        }
    }

    /**
     * 替换fragment函数
     */
    private fun replaceFragment(fragment: BaseFragment, tag: String) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment, tag)
        transaction.commit()
    }

    override fun onDataChanged() {
        val fragment = FragmentUtil.fragmentUtil.getFragment(R.id.tab_home) as MusicFragment
        fragment.onDataChanged()
    }

    override fun onTokenLoginSuccess(userResult: UserResultBean?) {
        myToast(getString(R.string.login_success))
        currentUser = userResult?.currentUser!!
        usernameText.text = currentUser.username
        usernameText.visibility = View.VISIBLE
        toLogin.visibility = View.GONE
        isLogin = true
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
        isLogin = false
        currentUser = UserBean(
            null, null, null,
            null, null, null, null,
            null, null, null, null, null,
            null, null
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
            UPDATE_AVATAR_REQUEST -> if (resultCode == RESULT_OK) {
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