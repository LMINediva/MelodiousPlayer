package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.UploadFileResultBean
import java.io.File

interface UploadMusicContract {

    interface Presenter : BasePresenter {
        fun uploadMusic(token: String, file: File)
    }

    interface View {
        fun onUploadMusicProgress(progress: Int, total: Long, current: Long, done: Boolean)
        fun onUploadMusicSuccess(result: UploadFileResultBean)
        fun onUploadMusicFailed()
        fun onNetworkError()
    }

}