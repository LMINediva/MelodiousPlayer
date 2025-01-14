package com.melodiousplayer.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.VideosBean

/**
 * MV每一个界面条目view
 */
class MVItemView : RelativeLayout {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        View.inflate(context, R.layout.item_mv, this)
    }

    /**
     * 适配每一个条目view
     */
    fun setData(data: VideosBean) {
        val artist: TextView = findViewById(R.id.artist)
        val title: TextView = findViewById(R.id.title)
        val bg: ImageView = findViewById(R.id.bg)
        // 歌手名称
        artist.text = data.artistName
        // 歌曲名称
        title.text = data.title
        // 背景图
        Glide.with(context).load(data.playListPic).into(bg)
    }

}