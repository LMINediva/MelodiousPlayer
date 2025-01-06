package com.melodiousplayer.android.view

import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.model.MVListBean

interface MVListView {

    /**
     * 获取数据失败
     */
    fun onError(message: String?)

    /**
     * 初始化数据或者刷新数据成功
     */
    fun loadSuccess(response: MVListBean?)

    /**
     * 加载更多成功
     */
    fun loadMore(response: MVListBean?)

}