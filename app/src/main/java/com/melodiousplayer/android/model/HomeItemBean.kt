package com.melodiousplayer.android.model

/**
 * 首页界面每个条目的bean
 */
data class HomeItemBean(
    var type: String, var id: Int, var title: String,
    var artistName: String, var description: String?,
    var posterPic: String?, var thumbnailPic: String?,
    var url: String, var hdUrl: String?, var uhdUrl: String?,
    var musicSize: Float, var hdMusicSize: Float, var uhdMusicSize: Float,
    var status: Int
)
