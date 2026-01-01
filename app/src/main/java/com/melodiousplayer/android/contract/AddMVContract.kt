package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.VideosBean

/**
 * 添加MV协议持久化接口
 */
interface AddMVContract {

    interface Presenter : BasePresenter {
        fun addMV(token: String, mv: VideosBean)
    }

    interface View {
        fun onArtistNameError()
        fun onDescriptionError()
        fun onMVPosterError()
        fun onMVThumbnailError()
        fun onMVFileError()
        fun onAddMVSuccess()
        fun onAddMVFailed()
        fun onNetworkError()
    }

}