package com.melodiousplayer.android.ui.activity

import android.widget.Button
import android.widget.EditText
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.RegisterContract
import com.melodiousplayer.android.presenter.impl.RegisterPresenterImpl

/**
 * 用户注册界面
 */
class RegisterActivity : BaseActivity(), RegisterContract.View {

    private lateinit var userName: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var register: Button
    private val presenter = RegisterPresenterImpl(this)

    override fun getLayoutId(): Int {
        return R.layout.activity_register
    }

    override fun initData() {
        userName = findViewById(R.id.userName)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirmPassword)
        register = findViewById(R.id.register)
    }

    override fun initListener() {
        register.setOnClickListener {
            register()
        }
        confirmPassword.setOnEditorActionListener { v, actionId, event ->
            register()
            true
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
        myToast(getString(R.string.register_failed))
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
        finish()
    }

    override fun onRegisterFailed() {
        dismissProgress()
        myToast(getString(R.string.register_failed))
    }

}