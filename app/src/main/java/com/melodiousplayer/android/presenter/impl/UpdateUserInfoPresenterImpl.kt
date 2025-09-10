package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UpdateUserInfoContract
import com.melodiousplayer.android.extension.isValidUserName
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.ResponseHandler

class UpdateUserInfoPresenterImpl(val view: UpdateUserInfoContract.View) :
    UpdateUserInfoContract.Presenter, ResponseHandler<ResultBean> {

    override fun updateUserInfo(userName: String, phoneNumber: String, email: String) {
        if (userName.isValidUserName()) {
            
        }
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        TODO("Not yet implemented")
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}