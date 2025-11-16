package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.ResultBean

/**
 * 删除MV协议持久化接口
 */
interface DeleteMVContract {

    interface Presenter : BasePresenter {
        fun deleteMV(token: String, ids: Array<Int>)
    }

    interface View {
        fun onDeleteMVSuccess(result: ResultBean)
        fun onDeleteMVFailed(result: ResultBean)
        fun onNetworkError()
    }

}