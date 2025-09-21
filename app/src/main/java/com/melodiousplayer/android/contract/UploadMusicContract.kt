package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadMusicContract {

    interface Presenter : BasePresenter {
        fun uploadMusic(token: String, file: File)
    }

    interface View {
        fun onUploadMusicSuccess(result: UploadFileResultBean)
        fun onUploadMusicFailed()
        fun onNetworkError()
    }

}