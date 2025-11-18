package com.melodiousplayer.android.model

import java.io.Serializable

data class MusicListBean(
    var totalCount: Int?,
    var playLists: List<PlayListsBean>?
)

data class PlayListsBean(
    var id: Int?,
    var title: String?,
    var thumbnailPic: String?,
    var videoCount: Int?,
    var mvList: MutableSet<VideosBean>?,
    var description: String?,
    var category: String?,
    var status: Int?,
    var totalViews: Int?,
    var totalFavorites: Int?,
    var updateTime: String?,
    var createTime: String?,
    var integral: Int?,
    var weekIntegral: Int?,
    var totalUser: Int?,
    var rank: Int?,
    var sysUser: UserBean?
) : Serializable