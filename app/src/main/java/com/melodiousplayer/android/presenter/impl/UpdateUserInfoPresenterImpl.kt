package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.melodiousplayer.android.contract.UpdateUserInfoContract
import com.melodiousplayer.android.extension.isValidEmail
import com.melodiousplayer.android.extension.isValidPhoneNumber
import com.melodiousplayer.android.extension.isValidUserName
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UpdateUserInfoRequest

class UpdateUserInfoPresenterImpl(val view: UpdateUserInfoContract.View) :
    UpdateUserInfoContract.Presenter, ResponseHandler<ResultBean> {

    override fun updateUserInfo(token: String, user: UserBean) {
        if (user.username?.isValidUserName() == true) {
            // 检查手机号码
            if (user.phonenumber?.isValidPhoneNumber() == true) {
                // 检查邮箱地址
                if (user.email?.isValidEmail() == true) {
                    // 更新用户信息
                    updateUserInfoRequest(token, user)
                } else {
                    view.onEmailError()
                }
            } else {
                view.onPhoneNumberError()
            }
        } else {
            view.onUserNameError()
        }
    }

    private fun updateUserInfoRequest(token: String, user: UserBean) {
        val json = Gson().toJson(user)
        UpdateUserInfoRequest(this).executePostWithJSON(token = token, json = json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 更新成功
            view.onUpdateSuccess()
        } else if (result.code == 501) {
            // 用户名已经存在
            view.onUserNameExistError()
        } else {
            // 注册失败
            view.onUpdateFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}