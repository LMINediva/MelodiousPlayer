package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.ResultBean

/**
 * 删除音乐协议持久化接口
 */
interface DeleteMusicContract {

    interface Presenter : BasePresenter {
        fun deleteMusic(token: String, ids: Array<Int>)
    }

    interface View {
        fun onDeleteMusicSuccess(result: ResultBean)
        fun onDeleteMusicFailed(result: ResultBean)
        fun onNetworkError()
    }

}