package com.melodiousplayer.android.adapter

import android.content.Context
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.widget.MyMusicItemView

class MyMusicAdapter : BaseListAdapter<MusicBean, MyMusicItemView>() {

    override fun refreshItemView(itemView: MyMusicItemView, data: MusicBean) {
        itemView.setData(data)
    }

    override fun getItemView(context: Context?): MyMusicItemView {
        return MyMusicItemView(context)
    }

}