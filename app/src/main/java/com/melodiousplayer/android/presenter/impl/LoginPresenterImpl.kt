package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.LoginContract
import com.melodiousplayer.android.extension.isValidPassword
import com.melodiousplayer.android.extension.isValidUserName
import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.net.LoginRequest
import com.melodiousplayer.android.net.ResponseHandler

class LoginPresenterImpl(val view: LoginContract.View) : LoginContract.Presenter,
    ResponseHandler<UserResultBean> {

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
        LoginRequest(userName, password, this).executePost(null)
    }

    override fun onSuccess(type: Int, result: UserResultBean) {
        if (result.code == 200) {
            view.onLoginSuccess()
        } else {
            view.onLoginFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        view.onNetworkError()
    }

}