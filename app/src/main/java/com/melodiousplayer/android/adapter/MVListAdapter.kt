package com.melodiousplayer.android.adapter

import android.content.Context
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.widget.MVItemView

/**
 * MV界面每一个list列表的适配器
 */
class MVListAdapter : BaseListAdapter<VideosBean, MVItemView>() {

    override fun refreshItemView(itemView: MVItemView, data: VideosBean) {
        itemView.setData(data)
    }

    override fun getItemView(context: Context?): MVItemView {
        return MVItemView(context)
    }

}