package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MyMusicListBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 我的悦单数据请求类
 */
class MyMusicListRequest(type: Int, handler: ResponseHandler<MyMusicListBean>) :
    MRequest<MyMusicListBean>(type, URLProviderUtils.postPagingMyMusicListUrl(), handler) {
}