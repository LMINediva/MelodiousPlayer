package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadPosterContract {

    interface Presenter : BasePresenter {
        fun uploadPoster(token: String, file: File)
    }

    interface View {
        fun onUploadPosterProgress(progress: Int, total: Long, current: Long, done: Boolean)
        fun onUploadPosterSuccess(result: UploadFileResultBean)
        fun onUploadPosterFailed()
        fun onNetworkError()
    }

}