package com.melodiousplayer.android.model

class MusicListBean {

    var totalCount: Int = 0
    var playLists: List<PlayListsBean>? = null

    class PlayListsBean {
        var id: Int = 0
        var title: String? = null
        var thumbnailPic: String? = null
        var videoCount: Int = 0
        var description: String? = null
        var category: String? = null
        var sysUser: UserBean? = null
        var status: Int = 0
        var totalViews: Int = 0
        var totalFavorites: Int = 0
        var updateTime: String? = null
        var createdTime: String? = null
        var integral: Int = 0
        var weekIntegral: Int = 0
        var totalUser: Int = 0
        var rank: Int = 0
    }

}