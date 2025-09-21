package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.base.BaseView
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.net.MusicRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.presenter.interf.MusicPresenter

class MusicPresenterImpl(var musicView: BaseView<List<MusicBean>>?) : MusicPresenter,
    ResponseHandler<List<MusicBean>> {

    /**
     * 解绑view和presenter
     */
    override fun destroyView() {
        if (musicView != null) {
            musicView = null
        }
    }

    /**
     * 加载数据失败
     */
    override fun onError(type: Int, msg: String?) {
        musicView?.onError(msg)
    }

    /**
     * 加载数据成功
     */
    override fun onSuccess(type: Int, result: List<MusicBean>) {
        // 区分是初始化数据还是加载更多数据
        when (type) {
            BaseListPresenter.TYPE_INIT_OR_REFRESH -> musicView?.loadSuccess(result)
            BaseListPresenter.TYPE_LOAD_MORE -> musicView?.loadMore(result)
        }
    }

    /**
     * 初始化数据或者刷新数据
     */
    override fun loadDatas() {
        // 定义request并发送request
        MusicRequest(BaseListPresenter.TYPE_INIT_OR_REFRESH, 0, this).execute()
    }

    /**
     * 加载更多数据
     */
    override fun loadMore(offset: Int) {
        // 定义request并发送request
        MusicRequest(BaseListPresenter.TYPE_LOAD_MORE, offset, this).execute()
    }

}