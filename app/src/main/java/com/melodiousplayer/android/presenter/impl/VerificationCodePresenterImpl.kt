package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.melodiousplayer.android.contract.VerificationCodeContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.VerificationCodeBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.VerificationCodeRequest

class VerificationCodePresenterImpl(val view: VerificationCodeContract.View) :
    VerificationCodeContract.Presenter, ResponseHandler<ResultBean> {

    override fun compareVerificationCode(inputVerificationCode: String) {
        val verificationCode = VerificationCodeBean(inputVerificationCode)
        val json = Gson().toJson(verificationCode)
        VerificationCodeRequest(this).executePostWithJSON(json = json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 比较成功
            view.onCompareVerificationCodeSuccess()
        } else if (result.code == 500) {
            // 比较失败
            view.onCompareVerificationCodeFailed(result)
        }
    }

    override fun onError(type: Int, msg: String?) {
        view.onNetworkError()
    }

}