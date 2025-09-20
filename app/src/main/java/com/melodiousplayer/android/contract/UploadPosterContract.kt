package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadPosterContract {

    interface Presenter : BasePresenter {
        fun uploadPoster(token: String, file: File)
    }

    interface View {
        fun onUploadPosterSuccess(result: UploadFileResultBean)
        fun onUploadPosterFailed()
        fun onNetworkError()
    }

}