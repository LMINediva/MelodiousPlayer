package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.util.URLProviderUtils

class DeleteMVRequest(handler: ResponseHandler<ResultBean>) :
    MRequest<ResultBean>(0, URLProviderUtils.postDeleteMV(), handler) {
}