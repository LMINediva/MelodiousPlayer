package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UploadPictureContract
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UploadFeedBackPictureRequest
import java.io.File

class UploadFeedBackPicturePresenterImpl(val view: UploadPictureContract.View) :
    UploadPictureContract.Presenter, ResponseHandler<UploadFileResultBean> {

    override fun uploadPicture(token: String, file: File) {
        UploadFeedBackPictureRequest(this).executePostUploadFile(token, "image/jpeg", file,
            progressCallback = { progress, total, current, done ->
                view.onUploadPictureProgress(progress, total, current, done)
            })
    }

    override fun onSuccess(type: Int, result: UploadFileResultBean) {
        if (result.code == 0) {
            view.onUploadPictureSuccess(result)
        } else {
            view.onUploadPictureFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}