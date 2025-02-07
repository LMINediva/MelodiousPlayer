package com.melodiousplayer.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.AudioBean

class PopupListItemView : RelativeLayout {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        View.inflate(context, R.layout.item_popup, this)
    }

    fun setData(data: AudioBean) {
        val title: TextView = findViewById(R.id.title)
        val artist: TextView = findViewById(R.id.artist)
        // 歌曲名
        title.text = data.displayName
        // 歌手名
        artist.text = data.artist
    }

}