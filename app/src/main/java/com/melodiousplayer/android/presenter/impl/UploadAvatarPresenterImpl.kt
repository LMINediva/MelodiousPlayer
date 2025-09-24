package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UploadAvatarContract
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UploadAvatarRequest
import java.io.File

class UploadAvatarPresenterImpl(val view: UploadAvatarContract.View) :
    UploadAvatarContract.Presenter, ResponseHandler<UploadFileResultBean> {

    override fun uploadAvatar(token: String, file: File) {
        UploadAvatarRequest(this).executePostUploadFile(token, "image/jpeg", file,
            progressCallback = { progress, total, current, done ->
                view.onUploadAvatarProgress(progress, total, current, done)
            })
    }

    override fun onSuccess(type: Int, result: UploadFileResultBean) {
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