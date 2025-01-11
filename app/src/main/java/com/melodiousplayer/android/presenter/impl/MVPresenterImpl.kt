package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.net.MVAreaRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.presenter.interf.MVPresenter
import com.melodiousplayer.android.view.MVView

class MVPresenterImpl(var mvView: MVView) : MVPresenter, ResponseHandler<List<MVAreaBean>> {

    /**
     * 加载区域数据
     */
    override fun loadDatas() {
        MVAreaRequest(this).execute()
    }

    override fun onError(type: Int, msg: String?) {
        mvView.onError(msg)
    }

    override fun onSuccess(type: Int, result: List<MVAreaBean>) {
        mvView.onSuccess(result)
    }

}