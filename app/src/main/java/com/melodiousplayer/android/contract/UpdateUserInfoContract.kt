package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UserBean

/**
 * 修改用户信息协议持久化接口
 */
interface UpdateUserInfoContract {

    interface Presenter : BasePresenter {
        fun updateUserInfo(token: String, user: UserBean)
    }

    interface View {
        fun onUserNameError()
        fun onUserNameExistError()
        fun onPhoneNumberError()
        fun onEmailError()
        fun onUpdateSuccess()
        fun onUpdateFailed()
        fun onNetworkError()
    }

}