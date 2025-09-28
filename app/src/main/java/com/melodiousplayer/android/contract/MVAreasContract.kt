package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.MVAreaBean

/**
 * 获取MV区域代码协议持久化接口
 */
interface MVAreasContract {

    interface Presenter : BasePresenter {
        fun getMVAreas(token: String)
    }

    interface View {
        fun onGetMVAreasSuccess(result: List<MVAreaBean>)
        fun onGetMVAreasFailed()
        fun onNetworkError()
    }

}