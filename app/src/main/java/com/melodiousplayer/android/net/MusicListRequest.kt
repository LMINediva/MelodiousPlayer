package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.MusicListBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 悦单界面网络请求request
 */
class MusicListRequest(type: Int, offset: Int, handler: ResponseHandler<MusicListBean>) :
    MRequest<MusicListBean>(type, URLProviderUtils.getMVListUrl(offset, 20), handler) {
}