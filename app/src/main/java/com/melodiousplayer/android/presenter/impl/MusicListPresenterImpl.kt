package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.model.MusicListBean
import com.melodiousplayer.android.net.MusicListRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.presenter.interf.MusicListPresenter
import com.melodiousplayer.android.view.MusicListView

class MusicListPresenterImpl(var musicListView: MusicListView?) : MusicListPresenter,
    ResponseHandler<MusicListBean> {

    override fun destroyView() {
        if (musicListView != null) {
            musicListView = null
        }
    }

    override fun loadDatas() {
        MusicListRequest(BaseListPresenter.TYPE_INIT_OR_REFRESH, 0, this).execute()
    }

    override fun loadMore(offset: Int) {
        MusicListRequest(BaseListPresenter.TYPE_LOAD_MORE, offset, this).execute()
    }

    override fun onError(type: Int, msg: String?) {
        musicListView?.onError(msg)
    }

    override fun onSuccess(type: Int, result: MusicListBean) {
        if (type == BaseListPresenter.TYPE_INIT_OR_REFRESH) {
            musicListView?.loadSuccess(result)
        } else if (type == BaseListPresenter.TYPE_LOAD_MORE) {
            musicListView?.loadMore(result)
        }
    }

}