package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MyMusicBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 我的音乐数据请求类
 */
class MyMusicRequest(type: Int, handler: ResponseHandler<MyMusicBean>) :
    MRequest<MyMusicBean>(type, URLProviderUtils.postPagingMyMusicUrl(), handler) {
}