package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadLyricContract {

    interface Presenter : BasePresenter {
        fun uploadLyric(token: String, file: File)
    }

    interface View {
        fun onUploadLyricProgress(progress: Int, total: Long, current: Long, done: Boolean)
        fun onUploadLyricSuccess(result: UploadFileResultBean)
        fun onUploadLyricFailed()
        fun onNetworkError()
    }

}