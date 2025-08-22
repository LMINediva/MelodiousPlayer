package com.melodiousplayer.android.ui.activity

import android.widget.EditText
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.LoginContract

/**
 * 用户登录界面
 */
class LoginActivity : BaseActivity(), LoginContract.View {

    private lateinit var userName: EditText
    private lateinit var password: EditText

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initData() {
        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)
    }

    override fun onUserNameError() {
        userName.error = getString(R.string.user_name_error)
    }

    override fun onPasswordError() {
        password.error = getString(R.string.password_error)
    }

    override fun onStartLogin() {
        // 弹出进度条
        showProgress(getString(R.string.logging))
    }

    override fun onLoginSuccess() {
        // 隐藏进度条
        dismissProgress()
        // 进入主界面，并退出LoginActivity
        startActivityAndFinish<MainActivity>()
    }

    override fun onLoginFailed() {
        // 隐藏进度条
        dismissProgress()
        // 弹出Toast
        myToast(getString(R.string.login_failed))
    }

}