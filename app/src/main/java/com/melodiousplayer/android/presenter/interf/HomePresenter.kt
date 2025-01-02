package com.melodiousplayer.android.presenter.interf

interface HomePresenter {
    /**
     * 初始化数据或者刷新数据
     */
    fun loadDatas()

    /**
     * 加载更多数据
     */
    fun loadMore(offset: Int)
}