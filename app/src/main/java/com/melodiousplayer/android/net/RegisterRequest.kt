package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.RegisterResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 用户注册网络请求
 */
class RegisterRequest(handler: ResponseHandler<RegisterResultBean>) :
    MRequest<RegisterResultBean>(0, URLProviderUtils.postRegisterUrl(), handler) {
}