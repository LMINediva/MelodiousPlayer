package com.melodiousplayer.android.adapter

import android.content.Context
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.widget.MusicListItemView

/**
 * 悦单界面适配器
 */
class MusicListAdapter : BaseListAdapter<PlayListsBean, MusicListItemView>() {

    override fun refreshItemView(itemView: MusicListItemView, data: PlayListsBean) {
        itemView.setData(data)
    }

    override fun getItemView(context: Context?): MusicListItemView {
        return MusicListItemView(context)
    }

}