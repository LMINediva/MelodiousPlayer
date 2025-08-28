package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UserResultBean

/**
 * Token登录协议持久化接口
 */
interface TokenLoginContract {

    interface Presenter : BasePresenter {
        fun tokenLogin(token: String)
    }

    interface View {
        fun onTokenLoginSuccess(userResult: UserResultBean?)
        fun onTokenLoginFailed(msg: String?)
        fun onNetworkError()
    }

}