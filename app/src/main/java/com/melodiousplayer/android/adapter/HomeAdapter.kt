package com.melodiousplayer.android.adapter

import android.content.Context
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.widget.MusicView

class HomeAdapter : BaseListAdapter<MusicBean, MusicView>() {

    override fun refreshItemView(itemView: MusicView, data: MusicBean) {
        itemView.setData(data)
    }

    override fun getItemView(context: Context?): MusicView {
        return MusicView(context)
    }

}