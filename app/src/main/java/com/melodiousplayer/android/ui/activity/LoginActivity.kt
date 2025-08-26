package com.melodiousplayer.android.ui.activity

import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.LoginContract
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.presenter.impl.LoginPresenterImpl

/**
 * 用户登录界面
 */
class LoginActivity : BaseActivity(), LoginContract.View {

    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var newUser: TextView
    private val presenter = LoginPresenterImpl(this)

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initData() {
        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        newUser = findViewById(R.id.newUser)
    }

    override fun initListener() {
        login.setOnClickListener {
            login()
        }
        password.setOnEditorActionListener { v, actionId, event ->
            login()
            true
        }
        newUser.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun login() {
        // 隐藏软键盘
        hideSoftKeyboard()
        val userNameString = userName.text.trim().toString()
        val passwordString = password.text.trim().toString()
        presenter.login(userNameString, passwordString)
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

    override fun onLoginSuccess(user: UserBean?) {
        // 隐藏进度条
        dismissProgress()
        // 弹出Toast
        myToast(getString(R.string.login_success))
        // 进入主界面，传递用户信息，并退出LoginActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", user)
        startActivity(intent)
        finish()
    }

    override fun onLoginFailed() {
        // 隐藏进度条
        dismissProgress()
        // 弹出Toast
        myToast(getString(R.string.login_failed))
    }

    override fun onNetworkError() {
        // 隐藏进度条
        dismissProgress()
        // 弹出Toast
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        // 当用户按下返回键时，跳转回到主界面
        startActivityAndFinish<MainActivity>()
        super.onBackPressed()
    }

}