package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.MusicBean

/**
 * 添加音乐协议持久化接口
 */
interface AddMusicContract {

    interface Presenter : BasePresenter {
        fun addMusic(token: String, music: MusicBean)
    }

    interface View {
        fun onMusicTitleError()
        fun onMusicTitleExistError()
        fun onArtistNameError()
        fun onDescriptionError()
        fun onMusicPosterError()
        fun onMusicThumbnailError()
        fun onMusicFileError()
        fun onAddMusicSuccess()
        fun onAddMusicFailed()
        fun onNetworkError()
    }

}