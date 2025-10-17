package com.melodiousplayer.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 我的悦单子项布局
 */
class MyMusicListItemView : RelativeLayout {

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
        View.inflate(context, R.layout.item_my_music_list, this)
    }

    /**
     * 刷新条目view数据
     */
    fun setData(data: PlayListsBean) {
        val title: TextView = findViewById(R.id.title)
        val count: TextView = findViewById(R.id.count)
        val bg: ImageView = findViewById(R.id.bg)
        // 悦单名称
        title.setText(data.title)
        // MV数量
        data.videoCount?.let { count.setText("MV数量：" + it) }
        // 背景图片
        Glide.with(context).load(
            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                    + URLProviderUtils.listPicturePath + data.thumbnailPic
        ).into(bg)
    }

}