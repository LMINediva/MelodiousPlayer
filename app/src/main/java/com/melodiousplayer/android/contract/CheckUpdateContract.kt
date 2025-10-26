package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.VersionUpdateResultBean

/**
 * APK检查更新协议持久化接口
 */
interface CheckUpdateContract {

    interface Presenter : BasePresenter {
        fun checkUpdate(version: String)
    }

    interface View {
        fun onCheckUpdateSuccess(result: VersionUpdateResultBean)
        fun onCheckUpdateFailed(msg: String?)
        fun onNetworkError()
    }

}