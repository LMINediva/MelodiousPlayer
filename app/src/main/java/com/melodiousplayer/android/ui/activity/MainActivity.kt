package com.melodiousplayer.android.ui.activity

import android.util.Log
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.base.InputDialogListener
import com.melodiousplayer.android.base.MessageListener
import com.melodiousplayer.android.base.OnDataChangedListener
import com.melodiousplayer.android.ui.fragment.HomeFragment
import com.melodiousplayer.android.ui.fragment.InputDialogFragment
import com.melodiousplayer.android.util.FragmentUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 主界面
 */
class MainActivity : BaseActivity(), ToolBarManager, InputDialogListener, MessageListener,
    OnDataChangedListener {

    private lateinit var bottomBar: BottomNavigationView

    // 惰性加载
    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initData() {
        initMainToolBar()
        bottomBar = findViewById(R.id.bottomBar)
        // 将首页添加到fragment中
        val homeFragment = FragmentUtil.fragmentUtil.getFragment(R.id.tab_home)
        if (homeFragment != null) {
            replaceFragment(homeFragment, R.id.tab_home.toString())
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

}