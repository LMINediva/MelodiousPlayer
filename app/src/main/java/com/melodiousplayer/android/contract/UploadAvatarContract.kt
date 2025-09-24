package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadAvatarContract {

    interface Presenter : BasePresenter {
        fun uploadAvatar(token: String, file: File)
    }

    interface View {
        fun onUploadAvatarProgress(progress: Int, total: Long, current: Long, done: Boolean)
        fun onUploadAvatarSuccess(result: UploadFileResultBean)
        fun onUploadAvatarFailed()
        fun onNetworkError()
    }

}