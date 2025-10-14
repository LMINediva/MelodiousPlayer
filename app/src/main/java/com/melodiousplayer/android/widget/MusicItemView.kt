package com.melodiousplayer.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 首页子项布局
 */
class MusicItemView : RelativeLayout {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    /**
     * 初始化方法
     */
    init {
        View.inflate(context, R.layout.item_music, this)
    }

    /**
     * 刷新条目view数据
     */
    fun setData(data: MusicBean) {
        val title: TextView = findViewById(R.id.title)
        val description: TextView = findViewById(R.id.description)
        val bg: ImageView = findViewById(R.id.bg)
        // 歌曲名称
        title.setText(data.title)
        // 简介
        description.setText(data.description)
        // 背景图片
        Glide.with(context).load(
            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                    + URLProviderUtils.musicImagePath + data.posterPic
        ).into(bg)
    }

}