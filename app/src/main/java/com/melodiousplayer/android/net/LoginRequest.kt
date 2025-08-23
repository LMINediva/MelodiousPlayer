package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 用户登录网络请求
 */
class LoginRequest(userName: String, password: String, handler: ResponseHandler<ResultBean>) :
    MRequest<ResultBean>(0, URLProviderUtils.postLoginUrl(userName, password), handler) {
}