package com.melodiousplayer.android.base

/**
 * 所有下拉刷新和上拉加载更多列表界面presenter的基类
 */
interface BaseListPresenter {

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

    /**
     * 解绑presenter和view
     */
    fun destroyView()

}