package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.MVListResultBean
import com.melodiousplayer.android.model.PageBean

/**
 * 获取MV列表协议持久化接口
 */
interface GetMVListContract {

    interface Presenter : BasePresenter {
        fun getMVList(token: String, pageBean: PageBean)
    }

    interface View {
        fun onGetMVListSuccess(result: MVListResultBean)
        fun onGetMVListFailed()
        fun onNetworkError()
    }

}