package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.VersionUpdateResultBean
import com.melodiousplayer.android.util.URLProviderUtils

class CheckUpdateRequest(handler: ResponseHandler<VersionUpdateResultBean>) :
    MRequest<VersionUpdateResultBean>(0, URLProviderUtils.postCheckUpdateUrl(), handler) {
}