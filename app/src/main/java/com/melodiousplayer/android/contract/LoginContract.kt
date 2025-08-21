package com.melodiousplayer.android.contract

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
        fun onStartLogin()
        fun onLoginSuccess()
        fun onLoginFailed()
    }

}