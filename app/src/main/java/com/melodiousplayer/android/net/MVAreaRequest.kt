package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * MV区域数据请求
 */
class MVAreaRequest(handler: ResponseHandler<List<MVAreaBean>>) :
    MRequest<List<MVAreaBean>>(0, URLProviderUtils.getMVareaUrl(), handler) {
}