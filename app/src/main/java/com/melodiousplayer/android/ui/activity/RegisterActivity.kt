package com.melodiousplayer.android.ui.activity

import android.content.Intent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.RegisterContract
import com.melodiousplayer.android.presenter.impl.RegisterPresenterImpl
import com.melodiousplayer.android.util.ThemeUtil

/**
 * 用户注册界面
 */
class RegisterActivity : BaseActivity(), RegisterContract.View, View.OnClickListener {

    private lateinit var registerHeader: LinearLayout
    private lateinit var registerContent: RelativeLayout
    private lateinit var back: ImageView
    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var register: Button
    private lateinit var toLogin: TextView
    private val presenter = RegisterPresenterImpl(this)

    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun initData() {
        registerHeader = findViewById(R.id.register_header)
        registerContent = findViewById(R.id.register_content)
        back = findViewById(R.id.back)
        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
        register = findViewById(R.id.register)
        toLogin = findViewById(R.id.toLogin)
        val isDarkTheme = ThemeUtil.isDarkTheme(this)
        if (isDarkTheme) {
            registerHeader.setBackgroundResource(R.color.black)
            registerContent.setBackgroundResource(R.drawable.card_night_background)
        }
    }

    override fun initListener() {
        back.setOnClickListener(this)
        register.setOnClickListener(this)
        toLogin.setOnClickListener(this)
        confirmPassword.setOnEditorActionListener { v, actionId, event ->
            register()
            true
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back -> {
                finish()
            }

            R.id.register -> {
                register()
            }

            R.id.toLogin -> {
                finish()
            }
        }
    }

    private fun register() {
        // 隐藏软键盘
        hideSoftKeyboard()
        val userNameString = userName.text.trim().toString()
        val passwordString = password.text.trim().toString()
        val confirmPasswordString = confirmPassword.text.trim().toString()
        presenter.register(userNameString, passwordString, confirmPasswordString)
    }

    override fun onUserNameError() {
        userName.error = getString(R.string.user_name_error)
    }

    override fun onUserNameExistError() {
        dismissProgress()
        myToast(getString(R.string.user_name_exist_error))
        userName.error = getString(R.string.user_name_exist_error)
    }

    override fun onPasswordError() {
        password.error = getString(R.string.password_error)
    }

    override fun onConfirmPasswordError() {
        confirmPassword.error = getString(R.string.confirm_password_error)
    }

    override fun onStartRegister() {
        showProgress(getString(R.string.registering))
    }

    override fun onRegisterSuccess() {
        dismissProgress()
        myToast(getString(R.string.register_success))
        val intent = Intent()
        intent.putExtra("username", userName.text.trim().toString())
        intent.putExtra("password", password.text.trim().toString())
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onRegisterFailed() {
        dismissProgress()
        myToast(getString(R.string.register_failed))
    }

    override fun onNetworkError() {
        dismissProgress()
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        // 当用户按下返回键时，返回登录界面
        finish()
        super.onBackPressed()
    }

}