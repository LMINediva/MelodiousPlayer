package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadPictureContract {

    interface Presenter : BasePresenter {
        fun uploadPicture(token: String, file: File)
    }

    interface View {
        fun onUploadPictureProgress(progress: Int, total: Long, current: Long, done: Boolean)
        fun onUploadPictureSuccess(result: UploadFileResultBean)
        fun onUploadPictureFailed()
        fun onNetworkError()
    }

}