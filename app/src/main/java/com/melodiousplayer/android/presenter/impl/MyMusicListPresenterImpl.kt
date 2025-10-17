package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.base.BaseView
import com.melodiousplayer.android.model.MyMusicListBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.net.MyMusicListRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.presenter.interf.MyMusicListPresenter

class MyMusicListPresenterImpl(
    var musicListView: BaseView<MyMusicListBean>?,
    var token: String,
    var pageBean: PageBean
) : MyMusicListPresenter, ResponseHandler<MyMusicListBean> {

    /**
     * 解绑view和presenter
     */
    override fun destroyView() {
        if (musicListView != null) {
            musicListView = null
        }
    }

    /**
     * 加载数据失败
     */
    override fun onError(type: Int, msg: String?) {
        musicListView?.onError(msg)
    }

    /**
     * 加载数据成功
     */
    override fun onSuccess(type: Int, result: MyMusicListBean) {
        // 区分是初始化数据还是加载更多数据
        when (type) {
            BaseListPresenter.TYPE_INIT_OR_REFRESH -> musicListView?.loadSuccess(result)
            BaseListPresenter.TYPE_LOAD_MORE -> musicListView?.loadMore(result)
        }
    }

    /**
     * 初始化数据或者刷新数据
     */
    override fun loadDatas() {
        // 定义request并发送request
        val json = Gson().toJson(pageBean)
        MyMusicListRequest(
            BaseListPresenter.TYPE_INIT_OR_REFRESH,
            this
        ).executePostWithJSON(token = token, json = json)
    }

    /**
     * 加载更多数据
     */
    override fun loadMore(offset: Int) {
        // 定义request并发送request
        pageBean.pageNum = offset
        val json = Gson().toJson(pageBean)
        MyMusicListRequest(
            BaseListPresenter.TYPE_LOAD_MORE,
            this
        ).executePostWithJSON(token = token, json = json)
    }

}