package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.UploadMusicContract
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.UploadMusicRequest
import java.io.File

class UploadMusicPresenterImpl(val view: UploadMusicContract.View) :
    UploadMusicContract.Presenter, ResponseHandler<UploadFileResultBean> {

    override fun uploadMusic(token: String, file: File) {
        UploadMusicRequest(this).executePostUploadFile(token, "audio/mpeg", file,
            progressCallback = { progress, total, current, done ->
                view.onUploadMusicProgress(progress, total, current, done)
            })
    }

    override fun onSuccess(type: Int, result: UploadFileResultBean) {
        if (result.code == 0) {
            view.onUploadMusicSuccess(result)
        } else {
            view.onUploadMusicFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}