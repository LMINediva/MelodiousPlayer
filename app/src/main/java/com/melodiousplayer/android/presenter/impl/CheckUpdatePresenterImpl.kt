package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.CheckUpdateContract
import com.melodiousplayer.android.model.VersionUpdateResultBean
import com.melodiousplayer.android.net.CheckUpdateRequest
import com.melodiousplayer.android.net.ResponseHandler

class CheckUpdatePresenterImpl(val view: CheckUpdateContract.View) :
    CheckUpdateContract.Presenter, ResponseHandler<VersionUpdateResultBean> {

    override fun checkUpdate(version: String) {
        val pair: Pair<String, Any> = Pair<String, Any>("version", version)
        CheckUpdateRequest(this).executePost(pair)
    }

    override fun onSuccess(type: Int, result: VersionUpdateResultBean) {
        if (result.code == 200) {
            // APK检查更新成功
            view.onCheckUpdateSuccess(result)
        } else {
            // APK检查更新失败
            view.onCheckUpdateFailed(result.msg)
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}