package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MVListResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * MV列表网络请求
 */
class MVListTokenRequest(handler: ResponseHandler<MVListResultBean>) :
    MRequest<MVListResultBean>(0, URLProviderUtils.postPagingMVUrl(), handler) {
}