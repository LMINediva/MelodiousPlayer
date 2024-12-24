package com.melodiousplayer.android.base

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
    protected fun initData() {

    }

    /**
     * adapter listener
     */
    protected fun initListener() {

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

}