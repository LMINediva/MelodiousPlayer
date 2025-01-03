package com.melodiousplayer.android.view

import com.melodiousplayer.android.model.HomeItemBean

/**
 * 首页界面和presenter层交互
 */
interface HomeView {

    /**
     * 获取数据失败
     */
    fun onError(message: String?)

    /**
     * 初始化数据或者刷新数据成功
     */
    fun loadSuccess(list: List<HomeItemBean>?)

    /**
     * 加载更多成功
     */
    fun loadMore(list: List<HomeItemBean>?)

}