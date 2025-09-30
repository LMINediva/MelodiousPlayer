package com.melodiousplayer.android.model

data class MVPagerBean(var totalCount: Int, var videos: List<VideosBean>)

data class VideosBean(
    var id: Int?,
    var title: String?,
    var description: String?,
    var mvArea: MVAreaBean?,
    var artistName: String?,
    var posterPic: String?,
    var thumbnailPic: String?,
    var regdate: String?,
    var videoSourceTypeName: String?,
    var totalViews: Int?,
    var totalPcViews: Int?,
    var totalMobileViews: Int?,
    var totalComments: Int?,
    var url: String?,
    var hdUrl: String?,
    var uhdUrl: String?,
    var videoSize: Float?,
    var hdVideoSize: Float?,
    var uhdVideoSize: Float?,
    var duration: String?,
    var status: Int?,
    var sysUser: UserBean?
)
