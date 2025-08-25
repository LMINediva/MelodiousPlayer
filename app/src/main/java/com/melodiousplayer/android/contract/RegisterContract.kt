package com.melodiousplayer.android.contract

/**
 * 注册协议持久化接口
 */
interface RegisterContract {

    interface Presenter : BasePresenter {
        fun register(userName: String, password: String, confirmPassword: String)
    }

    interface View {
        fun onUserNameError()
        fun onUserNameExistError()
        fun onPasswordError()
        fun onConfirmPasswordError()
        fun onStartRegister()
        fun onRegisterSuccess()
        fun onRegisterFailed()
    }

}