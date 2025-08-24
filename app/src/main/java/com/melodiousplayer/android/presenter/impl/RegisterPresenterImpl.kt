package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.RegisterContract
import com.melodiousplayer.android.extension.isValidPassword
import com.melodiousplayer.android.extension.isValidUserName

class RegisterPresenterImpl(val view: RegisterContract.View) : RegisterContract.Presenter {

    override fun register(userName: String, password: String, confirmPassword: String) {
        if (userName.isValidUserName()) {
            // 检查密码
            if (password.isValidPassword()) {
                // 检查确认密码
                if (password.equals(confirmPassword)) {
                    // 密码和确认密码一致
                    view.onStartRegister()
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

}