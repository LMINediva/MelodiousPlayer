package com.melodiousplayer.android.contract

/**
 * 修改用户信息协议持久化接口
 */
interface UpdateUserInfoContract {

    interface Presenter : BasePresenter {
        fun updateUserInfo(userName: String, phoneNumber: String, email: String)
    }

    interface View {
        fun onUserNameError()
        fun onUserNameExistError()
        fun onPhoneNumberError()
        fun onStartUpdate()
        fun onUpdateSuccess()
        fun onUpdateFailed()
        fun onNetworkError()
    }

}