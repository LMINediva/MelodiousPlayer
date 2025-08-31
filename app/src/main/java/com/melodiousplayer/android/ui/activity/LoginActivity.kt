package com.melodiousplayer.android.ui.activity

import android.content.Context
import android.content.Intent
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.LoginContract
import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.presenter.impl.LoginPresenterImpl
import com.melodiousplayer.android.util.EncryptUtil

/**
 * 用户登录界面
 */
class LoginActivity : BaseActivity(), LoginContract.View {
    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var newUser: TextView
    private lateinit var rememberPassword: CheckBox
    private val presenter = LoginPresenterImpl(this)

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initData() {
        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        newUser = findViewById(R.id.newUser)
        rememberPassword = findViewById(R.id.rememberPassword)
        val prefs = getSharedPreferences("data", Context.MODE_PRIVATE)
        val isRemember = prefs.getBoolean("remember_password", false)
        if (isRemember) {
            // 将账号和密码都设置到文本框中
            val storedUsername = prefs.getString("username", "")
            val storedPassword = prefs.getString("password", "")
            userName.setText(storedUsername)
            storedPassword?.let {
                val decryptedPassword = EncryptUtil.decrypt(it, EncryptUtil.key)
                password.setText(decryptedPassword)
            }
            rememberPassword.isChecked = true
        }
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

    override fun onLoginSuccess(userResult: UserResultBean?) {
        // 隐藏进度条
        dismissProgress()
        val editor = getSharedPreferences("data", Context.MODE_PRIVATE).edit()
        // 将token存储到SharedPreferences文件中
        editor.putString("token", userResult?.authorization)
        // 检查复选框是否被选中
        if (rememberPassword.isChecked) {
            editor.putBoolean("remember_password", true)
            editor.putString("username", userName.text.trim().toString())
            val encryptedPassword =
                EncryptUtil.encrypt(password.text.trim().toString(), EncryptUtil.key)
            editor.putString("password", encryptedPassword)
        } else {
            editor.remove("remember_password")
            editor.remove("username")
            editor.remove("password")
        }
        editor.apply()
        // 弹出Toast
        myToast(getString(R.string.login_success))
        // 进入主界面，传递用户信息，并退出LoginActivity
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("user", userResult?.currentUser)
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