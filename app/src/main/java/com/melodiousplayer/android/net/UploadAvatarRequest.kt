package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.UploadImageResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 上传头像网络请求
 */
class UploadAvatarRequest(handler: ResponseHandler<UploadImageResultBean>) :
    MRequest<UploadImageResultBean>(0, URLProviderUtils.postUploadAvatar(), handler) {
}