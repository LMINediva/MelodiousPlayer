package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UploadAvatarContract
import com.melodiousplayer.android.model.UploadImageResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UploadAvatarRequest
import java.io.File

class UploadAvatarPresenterImpl(val view: UploadAvatarContract.View) :
    UploadAvatarContract.Presenter, ResponseHandler<UploadImageResultBean> {

    override fun uploadAvatar(token: String, file: File) {
        UploadAvatarRequest(this).executePostUploadImage(token, file)
    }

    override fun onSuccess(type: Int, result: UploadImageResultBean) {
        if (result.code == 0) {
            view.onUploadAvatarSuccess(result)
        } else {
            view.onUploadAvatarFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}