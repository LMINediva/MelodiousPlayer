package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.model.MVListBean
import com.melodiousplayer.android.net.MVListRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.presenter.interf.MVListPresenter
import com.melodiousplayer.android.view.MVListView

class MVListPresenterImpl(var mvListView: MVListView) : MVListPresenter,
    ResponseHandler<MVListBean> {

    override fun loadDatas() {
        MVListRequest(MVListPresenter.TYPE_INIT_OR_REFRESH, 0, this).execute()
    }

    override fun loadMore(offset: Int) {

    }

    override fun onError(type: Int, msg: String?) {
        mvListView.onError(msg)
    }

    override fun onSuccess(type: Int, result: MVListBean) {
        if (type == MVListPresenter.TYPE_INIT_OR_REFRESH) {
            mvListView.loadSuccess(result)
        }
    }

}