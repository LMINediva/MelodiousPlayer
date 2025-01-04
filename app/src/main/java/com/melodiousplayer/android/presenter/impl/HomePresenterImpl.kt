package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.net.HomeRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.presenter.interf.HomePresenter
import com.melodiousplayer.android.view.HomeView

class HomePresenterImpl(var homeView: HomeView) : HomePresenter,
    ResponseHandler<List<HomeItemBean>> {

    /**
     * 加载数据失败
     */
    override fun onError(type: Int, msg: String?) {
        homeView.onError(msg)
    }

    /**
     * 加载数据成功
     */
    override fun onSuccess(type: Int, result: List<HomeItemBean>) {
        // 区分是初始化数据还是加载更多数据
        when (type) {
            HomePresenter.TYPE_INIT_OR_REFRESH -> homeView.loadSuccess(result)
            HomePresenter.TYPE_LOAD_MORE -> homeView.loadMore(result)
        }
    }

    /**
     * 初始化数据或者刷新数据
     */
    override fun loadDatas() {
        // 定义request并发送request
        val request = HomeRequest(HomePresenter.TYPE_INIT_OR_REFRESH, 0, this).execute()
    }

    /**
     * 加载更多数据
     */
    override fun loadMore(offset: Int) {
        // 定义request并发送request
        val request = HomeRequest(HomePresenter.TYPE_LOAD_MORE, offset, this).execute()
    }

}