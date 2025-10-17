package com.melodiousplayer.android.adapter

import android.content.Context
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.widget.MyMusicListItemView

class MyMusicListAdapter : BaseListAdapter<PlayListsBean, MyMusicListItemView>() {

    override fun refreshItemView(itemView: MyMusicListItemView, data: PlayListsBean) {
        itemView.setData(data)
    }

    override fun getItemView(context: Context?): MyMusicListItemView {
        return MyMusicListItemView(context)
    }

}