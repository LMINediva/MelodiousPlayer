package com.melodiousplayer.android.model

import java.io.Serializable

/**
 * 传递给视频播放界面的bean类
 */
data class VideoPlayBean(
    var id: Int,
    var title: String,
    var url: String,
    var thumbnailPic: String,
    var description: String
) : Serializable