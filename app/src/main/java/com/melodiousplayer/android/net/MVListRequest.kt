package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MVListBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 悦单界面网络请求request
 */
class MVListRequest(type: Int, offset: Int, handler: ResponseHandler<MVListBean>) :
    MRequest<MVListBean>(type, URLProviderUtils.getMVListUrl(offset, 20), handler) {
}