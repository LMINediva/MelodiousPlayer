package com.melodiousplayer.android.contract

/**
 * 用户退出登录协议持久化接口
 */
interface LogoutContract {

    interface Presenter : BasePresenter {
        fun logout()
    }

    interface View {
        fun onLogoutSuccess(msg: String?)
        fun onLogoutFailed(msg: String?)
        fun onNetworkError()
    }

}