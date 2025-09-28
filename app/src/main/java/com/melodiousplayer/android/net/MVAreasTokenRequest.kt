package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * MV区域网络请求
 */
class MVAreasTokenRequest(handler: ResponseHandler<List<MVAreaBean>>) :
    MRequest<List<MVAreaBean>>(0, URLProviderUtils.getMVAreas(), handler) {
}