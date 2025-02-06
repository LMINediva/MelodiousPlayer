package com.melodiousplayer.android.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.melodiousplayer.android.model.AudioBean

/**
 * 播放列表PopupWindow的适配器
 */
class PopupAdapter(var list: List<AudioBean>) : BaseAdapter() {

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(position: Int): Any {
        TODO("Not yet implemented")
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TODO("Not yet implemented")
    }

}