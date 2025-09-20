package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadLyricContract {

    interface Presenter : BasePresenter {
        fun uploadLyric(token: String, file: File)
    }

    interface View {
        fun onUploadLyricSuccess(result: UploadFileResultBean)
        fun onUploadLyricFailed()
        fun onNetworkError()
    }

}