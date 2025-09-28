package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadMVContract {

    interface Presenter : BasePresenter {
        fun uploadMV(token: String, file: File)
    }

    interface View {
        fun onUploadMVProgress(progress: Int, total: Long, current: Long, done: Boolean)
        fun onUploadMVSuccess(result: UploadFileResultBean)
        fun onUploadMVFailed()
        fun onNetworkError()
    }

}