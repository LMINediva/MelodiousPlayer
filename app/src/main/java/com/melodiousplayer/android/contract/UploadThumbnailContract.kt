package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadThumbnailContract {

    interface Presenter : BasePresenter {
        fun uploadThumbnail(token: String, file: File)
    }

    interface View {
        fun onUploadThumbnailProgress(progress: Int, total: Long, current: Long, done: Boolean)
        fun onUploadThumbnailSuccess(result: UploadFileResultBean)
        fun onUploadThumbnailFailed()
        fun onNetworkError()
    }

}