package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadImageResultBean
import java.io.File

interface UploadAvatarContract {

    interface Presenter : BasePresenter {
        fun uploadAvatar(token: String, file: File)
    }

    interface View {
        fun onUploadAvatarSuccess(result: UploadImageResultBean)
        fun onUploadAvatarFailed()
        fun onNetworkError()
    }

}