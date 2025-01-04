package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 首页数据请求类
 */
class HomeRequest(type: Int, offset: Int, handler: ResponseHandler<List<HomeItemBean>>) :
    MRequest<List<HomeItemBean>>(type, URLProviderUtils.getHomeUrl(offset, 20), handler) {
}