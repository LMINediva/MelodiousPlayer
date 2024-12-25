package com.melodiousplayer.android.base

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import splitties.toast.toast

/**
 * 所有activity的基类
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initListener()
        initData()
    }

    /**
     * 初始化数据
     */
    open protected fun initData() {

    }

    /**
     * adapter listener
     */
    open protected fun initListener() {

    }

    /**
     * 获取布局id
     */
    abstract fun getLayoutId(): Int

    /**
     * 实现可在子线程中安全的弹出消息
     */
    protected fun myToast(msg: String) {
        runOnUiThread { toast(msg) }
    }

    /**
     * 开启activity并且finish当前界面
     */
    inline fun <reified T : BaseActivity> startActivityAndFinish() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)
        finish()
    }

}