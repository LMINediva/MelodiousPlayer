package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.UserBean

/**
 * 修改用户登录密码协议持久化接口
 */
interface ChangePasswordContract {

    interface Presenter : BasePresenter {
        fun changePassword(token: String, user: UserBean, confirmPassword: String)
    }

    interface View {
        fun onOldPasswordError()
        fun onNewPasswordError()
        fun onConfirmPasswordError()
        fun onTwoPasswordsNotMatchError()
        fun onChangePasswordSuccess()
        fun onChangePasswordFailed(result: ResultBean)
        fun onNetworkError()
    }

}