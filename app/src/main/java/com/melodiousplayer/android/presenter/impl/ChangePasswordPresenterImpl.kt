package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.melodiousplayer.android.contract.ChangePasswordContract
import com.melodiousplayer.android.extension.isValidPassword
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.net.ChangePasswordRequest
import com.melodiousplayer.android.net.ResponseHandler

class ChangePasswordPresenterImpl(val view: ChangePasswordContract.View) :
    ChangePasswordContract.Presenter, ResponseHandler<ResultBean> {

    override fun changePassword(token: String, user: UserBean, confirmPassword: String) {
        // 检查旧密码
        if (user.oldPassword?.isValidPassword() == true) {
            // 检查新密码
            if (user.newPassword?.isValidPassword() == true) {
                // 检查确认密码
                if (confirmPassword.isValidPassword()) {
                    // 检查新密码和确认密码是否一致
                    if (user.newPassword.equals(confirmPassword)) {
                        // 修改用户登录密码
                        changePasswordRequest(token, user)
                    } else {
                        view.onTwoPasswordsNotMatchError()
                    }
                } else {
                    view.onConfirmPasswordError()
                }
            } else {
                view.onNewPasswordError()
            }
        } else {
            view.onOldPasswordError()
        }
    }

    private fun changePasswordRequest(token: String, user: UserBean) {
        val json = Gson().toJson(user)
        ChangePasswordRequest(this).executePostWithJSON(token = token, json = json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 修改用户登录密码成功
            view.onChangePasswordSuccess()
        } else if (result.code == 500) {
            // 修改用户登录密码失败
            view.onChangePasswordFailed(result)
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}