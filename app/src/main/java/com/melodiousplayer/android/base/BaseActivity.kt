package com.melodiousplayer.android.base

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * 所有activity的基类
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        initData()
        initListener()
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
        runOnUiThread {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 开启activity并且finish当前界面
     */
    inline fun <reified T : BaseActivity> startActivityAndFinish() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * 进度条弹窗
     */
    val progressDialog by lazy {
        ProgressDialog(this)
    }

    /**
     * 显示进度条弹窗
     */
    fun showProgress(message: String) {
        progressDialog.setMessage(message)
        progressDialog.show()
    }

    /**
     * 关闭进度条弹窗
     */
    fun dismissProgress() {
        progressDialog.dismiss()
    }

}