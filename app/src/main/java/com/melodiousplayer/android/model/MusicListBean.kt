package com.melodiousplayer.android.model

data class MusicListBean(
    var totalCount: Int?,
    var playLists: List<PlayListsBean>?
)

data class PlayListsBean(
    var id: Int?,
    var title: String?,
    var thumbnailPic: String?,
    var videoCount: Int?,
    var mvList: MutableList<VideosBean>?,
    var description: String?,
    var category: String?,
    var status: Int?,
    var totalViews: Int?,
    var totalFavorites: Int?,
    var updateTime: String?,
    var createdTime: String?,
    var integral: Int?,
    var weekIntegral: Int?,
    var totalUser: Int?,
    var rank: Int?,
    var sysUser: UserBean?
)