package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MVPagerBean
import com.melodiousplayer.android.net.MVListRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.presenter.interf.MVListPresenter
import com.melodiousplayer.android.view.MVListView

class MVListPresenterImpl(var code: String, var mvListView: MVListView) : MVListPresenter,
    ResponseHandler<MVPagerBean> {

    override fun loadDatas() {
        MVListRequest(BaseListPresenter.TYPE_INIT_OR_REFRESH, code, 0, this).execute()
    }

    override fun loadMore(offset: Int) {

    }

    override fun destroyView() {

    }

    override fun onError(type: Int, msg: String?) {

    }

    override fun onSuccess(type: Int, result: MVPagerBean) {

    }

}