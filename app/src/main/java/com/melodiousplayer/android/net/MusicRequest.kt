package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 首页数据请求类
 */
class MusicRequest(type: Int, offset: Int, handler: ResponseHandler<List<MusicBean>>) :
    MRequest<List<MusicBean>>(type, URLProviderUtils.getHomeUrl(offset, 20), handler) {
}