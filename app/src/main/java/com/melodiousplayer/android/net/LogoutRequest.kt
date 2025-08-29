package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.LogoutResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 用户退出登录网络请求
 */
class LogoutRequest(handler: ResponseHandler<LogoutResultBean>) :
    MRequest<LogoutResultBean>(0, URLProviderUtils.getLogoutUrl(), handler) {
}