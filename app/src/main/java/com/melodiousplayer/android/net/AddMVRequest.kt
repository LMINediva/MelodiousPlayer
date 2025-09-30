package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.util.URLProviderUtils

class AddMVRequest(handler: ResponseHandler<ResultBean>) :
    MRequest<ResultBean>(0, URLProviderUtils.postAddMV(), handler) {
}