package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UploadLyricContract
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UploadLyricRequest
import java.io.File

class UploadLyricPresenterImpl(val view: UploadLyricContract.View) :
    UploadLyricContract.Presenter, ResponseHandler<UploadFileResultBean> {

    override fun uploadLyric(token: String, file: File) {
        UploadLyricRequest(this).executePostUploadFile(token, "text/plain", file)
    }

    override fun onSuccess(type: Int, result: UploadFileResultBean) {
        if (result.code == 0) {
            view.onUploadLyricSuccess(result)
        } else {
            view.onUploadLyricFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}