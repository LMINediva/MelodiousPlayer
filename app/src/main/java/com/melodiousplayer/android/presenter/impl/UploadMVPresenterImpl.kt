package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UploadMVContract
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UploadMVRequest
import java.io.File

class UploadMVPresenterImpl(val view: UploadMVContract.View) :
    UploadMVContract.Presenter, ResponseHandler<UploadFileResultBean> {

    override fun uploadMV(token: String, file: File) {
        UploadMVRequest(this).executePostUploadFile(token, "video/mp4", file,
            progressCallback = { progress, total, current, done ->
                view.onUploadMVProgress(progress, total, current, done)
            })
    }

    override fun onSuccess(type: Int, result: UploadFileResultBean) {
        if (result.code == 0) {
            view.onUploadMVSuccess(result)
        } else {
            view.onUploadMVFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}