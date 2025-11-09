package com.melodiousplayer.android.ui.activity

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.LoginContract
import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.presenter.impl.LoginPresenterImpl
import com.melodiousplayer.android.util.EncryptUtil
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 用户登录界面
 */
class LoginActivity : BaseActivity(), LoginContract.View, View.OnClickListener {

    private lateinit var back: ImageView
    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var login: Button
    private lateinit var newUser: TextView
    private lateinit var rememberPassword: CheckBox
    private lateinit var verificationCode: EditText
    private lateinit var verificationCodeImage: ImageView
    private val REGISTER_REQUEST = 1
    private val presenter = LoginPresenterImpl(this)

    override fun getLayoutId(): Int {
        return R.layout.activity_login
    }

    override fun initData() {
        back = findViewById(R.id.back)
        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)
        login = findViewById(R.id.login)
        newUser = findViewById(R.id.newUser)
        rememberPassword = findViewById(R.id.rememberPassword)
        verificationCode = findViewById(R.id.verificationCode)
        verificationCodeImage = findViewById(R.id.verificationCodeImage)
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
        // 显示验证码，同时禁用Glide内存和磁盘缓存
        Glide.with(this)
            .load(
                URLProviderUtils.getVerificationCode()
            ).skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(verificationCodeImage)
    }

    override fun initListener() {
        back.setOnClickListener(this)
        login.setOnClickListener(this)
        password.setOnEditorActionListener { v, actionId, event ->
            login()
            true
        }
        newUser.setOnClickListener(this)
        verificationCodeImage.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                startActivityAndFinish<MainActivity>()
            }

            R.id.login -> {
                login()
            }

            R.id.newUser -> {
                startActivityForResult(Intent(this, RegisterActivity::class.java), REGISTER_REQUEST)
            }

            R.id.verificationCodeImage -> {
                Glide.with(this).load(
                    URLProviderUtils.getVerificationCode() + "?d=" + System.currentTimeMillis()
                ).into(verificationCodeImage)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REGISTER_REQUEST -> if (resultCode == RESULT_OK) {
                val userNameString = data?.getStringExtra("username")
                val passwordString = data?.getStringExtra("password")
                userName.setText(userNameString)
                password.setText(passwordString)
            }
        }
    }

    private fun login() {
        // 隐藏软键盘
        hideSoftKeyboard()
        val userNameString = userName.text.trim().toString()
        val passwordString = password.text.trim().toString()
        val verificationCodeString = verificationCode.text.trim().toString()
        presenter.login(userNameString, passwordString, verificationCodeString)
    }

    override fun onUserNameError() {
        userName.error = getString(R.string.user_name_error)
    }

    override fun onPasswordError() {
        password.error = getString(R.string.password_error)
    }

    override fun onVerificationCodeError(msg: String?) {
        runOnUiThread {
            verificationCode.error = msg
        }
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