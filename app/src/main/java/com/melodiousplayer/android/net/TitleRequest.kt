package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.ResultBean

class TitleRequest(url: String, handler: ResponseHandler<ResultBean>) :
    MRequest<ResultBean>(0, url, handler) {
}