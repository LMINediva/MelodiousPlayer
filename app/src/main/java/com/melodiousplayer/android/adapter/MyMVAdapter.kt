package com.melodiousplayer.android.adapter

import android.content.Context
import com.melodiousplayer.android.base.BaseListAdapter
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.widget.MyMVItemView

class MyMVAdapter : BaseListAdapter<VideosBean, MyMVItemView>() {

    override fun refreshItemView(itemView: MyMVItemView, data: VideosBean) {
        itemView.setData(data)
    }

    override fun getItemView(context: Context?): MyMVItemView {
        return MyMVItemView(context)
    }

}