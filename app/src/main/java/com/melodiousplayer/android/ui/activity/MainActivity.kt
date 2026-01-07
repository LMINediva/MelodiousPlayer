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
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.base.OnDataChangedListener
import com.melodiousplayer.android.contract.LogoutContract
import com.melodiousplayer.android.contract.TokenLoginContract
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.presenter.impl.LogoutPresenterImpl
import com.melodiousplayer.android.presenter.impl.TokenLoginPresenterImpl
import com.melodiousplayer.android.ui.fragment.LocalMusicFragment
import com.melodiousplayer.android.ui.fragment.MVFragment
import com.melodiousplayer.android.ui.fragment.MusicFragment
import com.melodiousplayer.android.ui.fragment.MusicListFragment
import com.melodiousplayer.android.util.ThemeUtil
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
    private lateinit var transaction: FragmentTransaction
    private val home = MusicFragment.newInstance()
    private val mv = MVFragment.newInstance()
    private val localMusicList = LocalMusicFragment.newInstance()
    private val musicList = MusicListFragment.newInstance()
    private val fragmentList = arrayListOf(home, mv, localMusicList, musicList)
    private val tagList = arrayListOf(
        R.id.tab_home.toString(), R.id.tab_mv.toString(),
        R.id.tab_local_music_list.toString(), R.id.tab_music_list.toString()
    )
    private var mFragment = 0
    private var toFragment = 0
    private var isLogin: Boolean = false
    private val PERMISSION_REQUEST = 1
    private val UPDATE_AVATAR_REQUEST = 1
    private val ADD_OR_EDIT_WORK_REQUEST = 2
    private val LOGIN_REQUEST = 3
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
        initMainToolBar(this)
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
        transaction = supportFragmentManager.beginTransaction()
        val isDarkTheme = ThemeUtil.isDarkTheme(this)
        if (isDarkTheme) {
            val bottomBarColor = getColor(R.color.black)
            bottomBar.setBackgroundColor(bottomBarColor)
        }
        val fragmentManager = supportFragmentManager
        val homeFragment = fragmentManager.findFragmentByTag(tagList[0])
        val mvFragment = fragmentManager.findFragmentByTag(tagList[1])
        val localMusicListFragment = fragmentManager.findFragmentByTag(tagList[2])
        val musicListFragment = fragmentManager.findFragmentByTag(tagList[3])
        if (homeFragment != null && !homeFragment.isRemoving && !homeFragment.isDetached) {
            // 首页 Fragment已添加
            if (!homeFragment.isHidden) {
                mFragment = 0
            }
        } else {
            // 将首页添加到fragment中
            transaction.add(R.id.container, home, tagList[0]).commit()
        }
        if (mvFragment != null && !mvFragment.isRemoving && !mvFragment.isDetached) {
            // MV Fragment已添加
            if (!mvFragment.isHidden) {
                mFragment = 1
            }
        }
        if (localMusicListFragment != null && !localMusicListFragment.isRemoving
            && !localMusicListFragment.isDetached
        ) {
            // 本地音乐 Fragment已添加
            if (!localMusicListFragment.isHidden) {
                mFragment = 2
            }
        }
        if (musicListFragment != null && !musicListFragment.isRemoving
            && !musicListFragment.isDetached
        ) {
            // 悦单 Fragment已添加
            if (!musicListFragment.isHidden) {
                mFragment = 3
            }
        }
        toLogin.setOnClickListener(this)
        // 从SharedPreferences文件中读取token的值
        val token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "")
        if (!token.isNullOrEmpty()) {
            presenter.tokenLogin(token)
        }
        menu = navView.menu
        // 隐藏侧边栏所有菜单项
        for (i in 0 until menu.size()) {
            menu.getItem(i).isVisible = false
        }
        requestPermissions()
    }

    override fun initListener() {
        // 设置tab切换监听
        bottomBar.setOnItemSelectedListener { item ->
            // item.itemId代表tabId参数
            transaction = supportFragmentManager.beginTransaction()
            when (item.itemId) {
                R.id.tab_home -> {
                    toFragment = 0
                    switchFragment(transaction)
                    mFragment = 0
                }

                R.id.tab_mv -> {
                    toFragment = 1
                    switchFragment(transaction)
                    mFragment = 1
                }

                R.id.tab_local_music_list -> {
                    toFragment = 2
                    switchFragment(transaction)
                    mFragment = 2
                }

                R.id.tab_music_list -> {
                    toFragment = 3
                    switchFragment(transaction)
                    mFragment = 3
                }
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
                startActivityForResult(intent, ADD_OR_EDIT_WORK_REQUEST)
            }

            R.id.navMyWorks -> {
                // 进入我的作品界面，传递用户信息
                val intent = Intent(this, MyWorkActivity::class.java)
                intent.putExtra("user", currentUser)
                startActivityForResult(intent, ADD_OR_EDIT_WORK_REQUEST)
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
                val intent = Intent(this, LoginActivity::class.java)
                startActivityForResult(intent, LOGIN_REQUEST)
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
     * 切换fragment函数
     */
    private fun switchFragment(transaction: FragmentTransaction) {
        val from = fragmentList[mFragment]
        val to = fragmentList[toFragment]
        val fromAdded = supportFragmentManager.findFragmentByTag(tagList[mFragment])
        val toAdded = supportFragmentManager.findFragmentByTag(tagList[toFragment])
        if (mFragment == toFragment) return
        if (fromAdded != null) {
            if (toAdded != null) {
                transaction.hide(fromAdded).show(toAdded).commit()
            } else {
                transaction.add(R.id.container, to, tagList[toFragment]).hide(fromAdded).show(to)
                    .commit()
            }
        } else {
            if (toAdded != null) {
                transaction.add(R.id.container, from, tagList[mFragment]).hide(from).show(toAdded)
                    .commit()
            } else {
                transaction.add(R.id.container, from, tagList[mFragment])
                    .add(R.id.container, to, tagList[toFragment]).hide(from).show(to).commit()
            }
        }
    }

    override fun onDataChanged() {
        val musicFragment = fragmentList[0] as MusicFragment
        if (musicFragment.isAdded) {
            musicFragment.onDataChanged()
        }
    }

    override fun onTokenLoginSuccess(userResult: UserResultBean?) {
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
            LOGIN_REQUEST -> if (resultCode == RESULT_OK) {
                userSerialized = data?.getSerializableExtra("user")
                if (userSerialized != null) {
                    // 登录成功显示用户名和头像
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
            }

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

            ADD_OR_EDIT_WORK_REQUEST -> if (resultCode == RESULT_OK) {
                val addOrModifyMusicSuccess =
                    data?.getBooleanExtra("addOrModifyMusicSuccess", false)
                val addOrModifyMVSuccess = data?.getBooleanExtra("addOrModifyMVSuccess", false)
                val addOrModifyMusicListSuccess =
                    data?.getBooleanExtra("addOrModifyMusicListSuccess", false)
                if (addOrModifyMusicSuccess == true) {
                    val musicFragment = fragmentList[0] as MusicFragment
                    if (musicFragment.isAdded) {
                        musicFragment.onDataChanged()
                    }
                }
                if (addOrModifyMVSuccess == true) {
                    val mvFragment = fragmentList[1] as MVFragment
                    if (mvFragment.isAdded) {
                        mvFragment.presenter.loadDatas()
                    }
                }
                if (addOrModifyMusicListSuccess == true) {
                    val musicListFragment = fragmentList[3] as MusicListFragment
                    if (musicListFragment.isAdded) {
                        musicListFragment.onDataChanged()
                    }
                }
            }
        }
    }

}