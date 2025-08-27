package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.UserResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 用户Token登录网络请求
 */
class TokenLoginRequest(handler: ResponseHandler<UserResultBean>) :
    MRequest<UserResultBean>(0, URLProviderUtils.postTokenLoginUrl(), handler) {
}