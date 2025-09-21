package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.ResultBean

/**
 * 获取歌词内容协议持久化接口
 */
interface GetLyricTextContract {

    interface Presenter : BasePresenter {
        fun getLyricText(lyricFileName: String)
    }

    interface View {
        fun onGetLyricTextSuccess(result: ResultBean)
        fun onGetLyricTextFailed()
        fun onNetworkError()
    }

}