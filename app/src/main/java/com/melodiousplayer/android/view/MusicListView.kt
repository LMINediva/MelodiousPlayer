package com.melodiousplayer.android.view

import com.melodiousplayer.android.model.MusicListBean

interface MusicListView {

    /**
     * 获取数据失败
     */
    fun onError(message: String?)

    /**
     * 初始化数据或者刷新数据成功
     */
    fun loadSuccess(response: MusicListBean?)

    /**
     * 加载更多成功
     */
    fun loadMore(response: MusicListBean?)

}