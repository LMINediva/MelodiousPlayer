package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UploadThumbnailContract
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UploadMVThumbnailRequest
import java.io.File

class UploadMVThumbnailPresenterImpl(val view: UploadThumbnailContract.View) :
    UploadThumbnailContract.Presenter, ResponseHandler<UploadFileResultBean> {

    override fun uploadThumbnail(token: String, file: File) {
        UploadMVThumbnailRequest(this).executePostUploadFile(token, "image/jpeg", file,
            progressCallback = { progress, total, current, done ->
                view.onUploadThumbnailProgress(progress, total, current, done)
            })
    }

    override fun onSuccess(type: Int, result: UploadFileResultBean) {
        if (result.code == 0) {
            view.onUploadThumbnailSuccess(result)
        } else {
            view.onUploadThumbnailFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}