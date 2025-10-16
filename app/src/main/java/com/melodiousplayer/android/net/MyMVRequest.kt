package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MyMVBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 我的MV数据请求类
 */
class MyMVRequest(type: Int, handler: ResponseHandler<MyMVBean>) :
    MRequest<MyMVBean>(type, URLProviderUtils.postPagingMyMVUrl(), handler) {
}