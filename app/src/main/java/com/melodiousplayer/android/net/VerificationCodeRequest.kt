package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 比较验证码网络请求
 */
class VerificationCodeRequest(handler: ResponseHandler<ResultBean>) :
    MRequest<ResultBean>(0, URLProviderUtils.postCompareVerificationCode(), handler) {
}