package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.ResultBean

/**
 * 验证码协议持久化接口
 */
interface VerificationCodeContract {

    interface Presenter : BasePresenter {
        fun compareVerificationCode(inputVerificationCode: String)
    }

    interface View {
        fun onCompareVerificationCodeSuccess()
        fun onCompareVerificationCodeFailed(result: ResultBean?)
        fun onNetworkError()
    }

}