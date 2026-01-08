package com.melodiousplayer.android.widget

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
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
        val titleAndArtist: TextView = findViewById(R.id.titleAndArtist)
        // 歌手名
        val artistName = SpannableString(data.artist?.trim())
        val textColor = context.getColor(R.color.textGray)
        val start = 0
        val end = artistName.length
        val colorSpan = ForegroundColorSpan(textColor)
        artistName.setSpan(colorSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        // 歌曲名和歌手名
        titleAndArtist.text = data.displayName.trim() + "    "
        titleAndArtist.append(artistName)
    }

}