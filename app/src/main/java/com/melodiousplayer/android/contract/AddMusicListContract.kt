package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.PlayListsBean

/**
 * 添加悦单协议持久化接口
 */
interface AddMusicListContract {

    interface Presenter : BasePresenter {
        fun addMusicList(token: String, play: PlayListsBean)
    }

    interface View {
        fun onMusicListTitleError()
        fun onMusicListTitleExistError()
        fun onDescriptionError()
        fun onCategoryError()
        fun onMusicListThumbnailError()
        fun onMusicListMVQuantityError()
        fun onAddMusicListSuccess()
        fun onAddMusicListFailed()
        fun onNetworkError()
    }

}