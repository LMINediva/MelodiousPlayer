package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.FeedBackBean

/**
 * 添加音乐协议持久化接口
 */
interface AddFeedBackContract {

    interface Presenter : BasePresenter {
        fun addFeedBack(token: String, feedback: FeedBackBean)
    }

    interface View {
        fun onFeedBackContentError()
        fun onFeedBackContentLengthError()
        fun onAddFeedBackSuccess()
        fun onAddFeedBackFailed()
        fun onNetworkError()
    }

}