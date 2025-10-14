package com.melodiousplayer.android.adapter

import android.content.Context
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.widget.MusicItemView

class MusicAdapter : BaseListAdapter<MusicBean, MusicItemView>() {

    override fun refreshItemView(itemView: MusicItemView, data: MusicBean) {
        itemView.setData(data)
    }

    override fun getItemView(context: Context?): MusicItemView {
        return MusicItemView(context)
    }

}