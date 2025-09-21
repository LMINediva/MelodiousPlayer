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
        View.inflate(context, R.layout.item_mv_subitem, this)
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
        // 悦单标题
        title.text = "标题：" + data.title
        // 悦单创建者用户名
        author_name.text = "创建者：" + data.sysUser?.username
        // 悦单中MV的数量
        count.text = "MV数量：" + data.videoCount.toString()
        // 背景
        Glide.with(context).load(
            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                    + URLProviderUtils.listPicturePath + data.thumbnailPic
        ).into(bg)
        // 悦单创建者头像
        Glide.with(context).load(
            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                    + URLProviderUtils.userAvatarPath + data.sysUser?.avatar
        )
            .transform(CircleCrop()).into(author_image)
    }

}