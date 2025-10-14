package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.melodiousplayer.android.base.BaseListPresenter
import com.melodiousplayer.android.base.BaseView
import com.melodiousplayer.android.model.MyMusicBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.net.MyMusicRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.presenter.interf.MyMusicPresenter

class MyMusicPresenterImpl(
    var musicView: BaseView<MyMusicBean>?,
    var token: String,
    var pageBean: PageBean
) : MyMusicPresenter, ResponseHandler<MyMusicBean> {

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
    override fun onSuccess(type: Int, result: MyMusicBean) {
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
        val json = Gson().toJson(pageBean)
        MyMusicRequest(
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
        MyMusicRequest(
            BaseListPresenter.TYPE_LOAD_MORE,
            this
        ).executePostWithJSON(token = token, json = json)
    }

}