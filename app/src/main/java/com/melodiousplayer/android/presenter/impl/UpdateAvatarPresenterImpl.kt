package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.melodiousplayer.android.contract.UpdateAvatarContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UpdateAvatarRequest

class UpdateAvatarPresenterImpl(val view: UpdateAvatarContract.View) :
    UpdateAvatarContract.Presenter, ResponseHandler<ResultBean> {

    override fun updateAvatar(token: String, user: UserBean) {
        val json = Gson().toJson(user)
        UpdateAvatarRequest(this).executePostWithJSON(token, json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            view.onUpdateAvatarSuccess()
        } else {
            view.onUpdateAvatarFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}