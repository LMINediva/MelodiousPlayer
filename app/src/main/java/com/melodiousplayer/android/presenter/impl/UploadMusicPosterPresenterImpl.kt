package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UploadPosterContract
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UploadMusicPosterRequest
import java.io.File

class UploadMusicPosterPresenterImpl(val view: UploadPosterContract.View) :
    UploadPosterContract.Presenter, ResponseHandler<UploadFileResultBean> {

    override fun uploadPoster(token: String, file: File) {
        UploadMusicPosterRequest(this).executePostUploadFile(token, "image/jpeg", file,
            progressCallback = { progress, total, current, done ->
                view.onUploadPosterProgress(progress, total, current, done)
            })
    }

    override fun onSuccess(type: Int, result: UploadFileResultBean) {
        if (result.code == 0) {
            view.onUploadPosterSuccess(result)
        } else {
            view.onUploadPosterFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}