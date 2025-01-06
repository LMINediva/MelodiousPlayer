package com.melodiousplayer.android.presenter.interf

interface MVListPresenter {

    companion object {
        val TYPE_INIT_OR_REFRESH = 1
        val TYPE_LOAD_MORE = 2
    }

    /**
     * 初始化数据或者刷新数据
     */
    fun loadDatas()

    /**
     * 加载更多数据
     */
    fun loadMore(offset: Int)

}