package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.ResultBean

/**
 * 删除音乐清单协议持久化接口
 */
interface DeleteMusicListContract {

    interface Presenter : BasePresenter {
        fun deleteMusicList(token: String, ids: Array<Int>)
    }

    interface View {
        fun onDeleteMusicListSuccess(result: ResultBean)
        fun onDeleteMusicListFailed(result: ResultBean)
        fun onNetworkError()
    }

}