package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.melodiousplayer.android.contract.RegisterContract
import com.melodiousplayer.android.extension.isValidPassword
import com.melodiousplayer.android.extension.isValidUserName
import com.melodiousplayer.android.model.RegisterResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.net.RegisterRequest
import com.melodiousplayer.android.net.ResponseHandler

class RegisterPresenterImpl(val view: RegisterContract.View) : RegisterContract.Presenter,
    ResponseHandler<RegisterResultBean> {

    override fun register(userName: String, password: String, confirmPassword: String) {
        if (userName.isValidUserName()) {
            // 检查密码
            if (password.isValidPassword()) {
                // 检查确认密码
                if (password.equals(confirmPassword)) {
                    // 密码和确认密码一致
                    view.onStartRegister()
                    // 开始注册
                    registerRequest(userName, password)
                } else {
                    view.onConfirmPasswordError()
                }
            } else {
                view.onPasswordError()
            }
        } else {
            view.onUserNameError()
        }
    }

    private fun registerRequest(userName: String, password: String) {
        val user = UserBean(
            null, userName, password,
            null, null, null, null,
            null, null, null, null, null,
            null, null
        )
        val json = Gson().toJson(user)
        RegisterRequest(this).executePostWithJSON(json = json)
    }

    override fun onSuccess(type: Int, result: RegisterResultBean) {
        if (result.code == 200) {
            // 注册成功
            view.onRegisterSuccess()
        } else if (result.code == 501) {
            // 用户名已经存在
            view.onUserNameExistError()
        } else {
            // 注册失败
            view.onRegisterFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}