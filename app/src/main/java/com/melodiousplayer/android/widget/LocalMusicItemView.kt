package com.melodiousplayer.android.widget

import android.content.Context
import android.text.format.Formatter
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.AudioBean

class LocalMusicItemView : RelativeLayout {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        View.inflate(context, R.layout.item_local_music, this)
    }

    /**
     * view和data绑定
     */
    fun setData(itemBean: AudioBean) {
        val title: TextView = findViewById(R.id.title)
        val artist: TextView = findViewById(R.id.artist)
        val size: TextView = findViewById(R.id.size)
        // 歌曲名
        title.text = itemBean.displayName
        // 歌手名
        artist.text = itemBean.artist
        // 歌曲大小
        size.text = Formatter.formatFileSize(context, itemBean.size)
    }

}