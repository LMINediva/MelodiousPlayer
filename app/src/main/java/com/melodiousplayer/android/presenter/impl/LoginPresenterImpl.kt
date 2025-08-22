package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.LoginContract
import com.melodiousplayer.android.extension.isValidPassword
import com.melodiousplayer.android.extension.isValidUserName

class LoginPresenterImpl(val view: LoginContract.View) : LoginContract.Presenter {

    override fun login(userName: String, password: String) {
        if (userName.isValidUserName()) {
            // 用户名合法，继续校验密码
            if (password.isValidPassword()) {
                // 密码合法，开始登录
                view.onStartLogin()
                // 登录到后端服务器
                loginRequest(userName, password)
            } else {
                view.onPasswordError()
            }
        } else {
            view.onUserNameError()
        }
    }

    private fun loginRequest(userName: String, password: String) {
        TODO("Not yet implemented")
    }

}