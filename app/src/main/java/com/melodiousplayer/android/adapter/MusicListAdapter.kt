package com.melodiousplayer.android.adapter

import android.content.Context
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.model.MusicListBean
import com.melodiousplayer.android.widget.MusicListItemView

/**
 * 悦单界面适配器
 */
class MusicListAdapter : BaseListAdapter<MusicListBean.PlayListsBean, MusicListItemView>() {

    override fun refreshItemView(itemView: MusicListItemView, data: MusicListBean.PlayListsBean) {
        itemView.setData(data)
    }

    override fun getItemView(context: Context?): MusicListItemView {
        return MusicListItemView(context)
    }

}