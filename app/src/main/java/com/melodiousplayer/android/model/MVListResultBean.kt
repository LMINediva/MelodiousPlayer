package com.melodiousplayer.android.model

data class MVListResultBean(
    var code: Int?,
    var mvList: MutableList<VideosBean>?,
    var total: Long?
)
