package com.melodiousplayer.android.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.MusicListBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 悦单界面每个条目的自定义view
 */
class MusicListItemView : RelativeLayout {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        View.inflate(context, R.layout.item_music, this)
    }

    /**
     * 条目view的控件初始化
     */
    fun setData(data: MusicListBean.PlayListsBean) {
        val title: TextView = findViewById(R.id.title)
        val author_name: TextView = findViewById(R.id.author_name)
        val count: TextView = findViewById(R.id.count)
        val bg: ImageView = findViewById(R.id.bg)
        val author_image: ImageView = findViewById(R.id.author_image)
        // MV列表名称
        title.text = data.title
        // 歌手名称
        author_name.text = data.creator?.nickName
        // MV列表数量
        count.text = data.videoCount.toString()
        // 背景
        Glide.with(context).load(
            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                    + data.playListBigPic
        ).into(bg)
        // 歌手头像
        Glide.with(context).load(
            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                    + data.creator?.largeAvatar
        )
            .transform(CircleCrop()).into(author_image)
    }

}