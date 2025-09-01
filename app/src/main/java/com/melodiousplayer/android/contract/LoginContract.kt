package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UserResultBean

/**
 * 登录协议持久化接口
 */
interface LoginContract {

    interface Presenter : BasePresenter {
        fun login(userName: String, password: String)
    }

    interface View {
        fun onUserNameError()
        fun onPasswordError()
        fun onVerificationCodeError(msg: String?)
        fun onStartLogin()
        fun onLoginSuccess(userResult: UserResultBean?)
        fun onLoginFailed()
        fun onNetworkError()
    }

}