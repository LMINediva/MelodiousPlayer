package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.ResultBean

/**
 * 标题重复检查协议持久化接口
 */
interface TitleContract {

    interface Presenter : BasePresenter {
        fun checkTitle(token: String, url: String, title: String, json: String)
    }

    interface View {
        fun onTitleError()
        fun onCheckTitleSuccess()
        fun onCheckTitleFailed(result: ResultBean)
        fun onNetworkError()
    }

}