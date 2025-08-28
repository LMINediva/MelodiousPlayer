package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.TokenLoginContract
import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.TokenLoginRequest

class TokenLoginPresenterImpl(val view: TokenLoginContract.View) : TokenLoginContract.Presenter,
    ResponseHandler<UserResultBean> {

    override fun tokenLogin(token: String) {
        tokenLoginRequest(token)
    }

    private fun tokenLoginRequest(token: String) {
        TokenLoginRequest(this).execute(token)
    }

    override fun onSuccess(type: Int, result: UserResultBean) {
        if (result.currentUser !== null) {
            view.onTokenLoginSuccess(result)
        } else {
            view.onTokenLoginFailed(result.msg)
        }
    }

    override fun onError(type: Int, msg: String?) {
        view.onNetworkError()
    }

}