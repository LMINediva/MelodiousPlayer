package com.melodiousplayer.android.contract

import java.io.File

interface UploadAvatarContract {

    interface Presenter : BasePresenter {
        fun uploadImage(file: File)
    }

    interface View {
        fun onUploadImageSuccess()
        fun onUploadImageFailed()
        fun onNetworkError()
    }

}