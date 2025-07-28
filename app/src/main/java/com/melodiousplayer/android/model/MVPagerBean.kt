package com.melodiousplayer.android.model

data class MVPagerBean(var totalCount: Int, var videos: List<VideosBean>)

data class VideosBean(
    var id: Int,
    var title: String,
    var description: String,
    var artistName: String,
    var posterPic: String,
    var thumbnailPic: String,
    var regdate: String,
    var videoSourceTypeName: String,
    var totalViews: Int,
    var totalPcViews: Int,
    var totalMobileViews: Int,
    var totalComments: Int,
    var url: String,
    var hdUrl: String,
    var uhdUrl: String,
    var videoSize: Float,
    var hdVideoSize: Float,
    var uhdVideoSize: Float,
    var duration: String,
    var status: Int
)

//data class ArtistsBean(var id: Int, var artistName: String)
//class MvPagerBean {
//    var totalCount: Int = 0
//    var videos: List<VideosBean>? = null
//
//    class VideosBean {
//
//        var id: Int = 0
//        var title: String? = null
//        var description: String? = null
//        var artistName: String? = null
//        var posterPic: String? = null
//        var thumbnailPic: String? = null
//        var albumImg: String? = null
//        var regdate: String? = null
//        var videoSourceTypeName: String? = null
//        var totalViews: Int = 0
//        var totalPcViews: Int = 0
//        var totalMobileViews: Int = 0
//        var totalComments: Int = 0
//        var url: String? = null
//        var hdUrl: String? = null
//        var uhdUrl: String? = null
//        var shdUrl: String? = null
//        var videoSize: Int = 0
//        var hdVideoSize: Int = 0
//        var uhdVideoSize: Int = 0
//        var shdVideoSize: Int = 0
//        var duration: Int = 0
//        var status: Int = 0
//        var linkId: Int = 0
//        var playListPic: String? = null
//        var artists: List<ArtistsBean>? = null
//
//        class ArtistsBean {
//            var artistId: Int = 0
//            var artistName: String? = null
//        }
//    }
//}