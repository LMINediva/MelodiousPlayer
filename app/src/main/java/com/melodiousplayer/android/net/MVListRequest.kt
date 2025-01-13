package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MVPagerBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * MV每一个界面数据网络请求
 */
class MVListRequest(type: Int, code: String, offset: Int, handler: ResponseHandler<MVPagerBean>) :
    MRequest<MVPagerBean>(type, URLProviderUtils.getMVAreaListUrl(code, offset, 20), handler) {
}