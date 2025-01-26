package com.melodiousplayer.android.adapter

import android.content.Context
import android.database.Cursor
import android.view.View
import android.view.ViewGroup
import androidx.cursoradapter.widget.CursorAdapter
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.widget.LocalMusicItemView

/**
 * 本地音乐界面列表适配器
 */
class LocalMusicAdapter(context: Context?, c: Cursor?) : CursorAdapter(context, c) {

    /**
     * 创建条目view
     */
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LocalMusicItemView(context)
    }

    /**
     * view和data的绑定
     */
    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        // view
        val itemView = view as LocalMusicItemView
        // data
        val itemBean = AudioBean.getAudioBean(cursor)
        // view和data绑定
        itemView.setData(itemBean)
    }

}