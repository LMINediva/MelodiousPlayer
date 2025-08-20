package com.melodiousplayer.android.model

import java.util.Date

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
        var sysUser: SysUserBean? = null
        var status: Int = 0
        var totalViews: Int = 0
        var totalFavorites: Int = 0
        var updateTime: String? = null
        var createdTime: String? = null
        var integral: Int = 0
        var weekIntegral: Int = 0
        var totalUser: Int = 0
        var rank: Int = 0

        class SysUserBean {
            var id: Int = 0
            var username: String? = null
            var avatar: String? = null
            var email: String? = null
            var phonenumber: String? = null
            var loginDate: Date? = null
            var status: String? = null
            var createTime: Date? = null
            var updateTime: Date? = null
            var remark: String? = null
        }
    }

}