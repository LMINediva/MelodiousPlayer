package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadThumbnailContract {

    interface Presenter : BasePresenter {
        fun uploadThumbnail(token: String, file: File)
    }

    interface View {
        fun onUploadThumbnailSuccess(result: UploadFileResultBean)
        fun onUploadThumbnailFailed()
        fun onNetworkError()
    }

}