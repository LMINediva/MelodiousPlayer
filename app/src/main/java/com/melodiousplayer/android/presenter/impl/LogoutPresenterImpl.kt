package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.LogoutContract
import com.melodiousplayer.android.model.LogoutResultBean
import com.melodiousplayer.android.net.LogoutRequest
import com.melodiousplayer.android.net.ResponseHandler

class LogoutPresenterImpl(val view: LogoutContract.View) : LogoutContract.Presenter,
    ResponseHandler<LogoutResultBean> {

    override fun logout() {
        logoutRequest()
    }

    private fun logoutRequest() {
        LogoutRequest(this).execute()
    }

    override fun onSuccess(type: Int, result: LogoutResultBean) {
        if (result.code == 200) {
            view.onLogoutSuccess(result.msg)
        } else {
            view.onLogoutFailed(result.msg)
        }
    }

    override fun onError(type: Int, msg: String?) {
        view.onNetworkError()
    }

}