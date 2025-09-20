package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 上传头像网络请求
 */
class UploadAvatarRequest(handler: ResponseHandler<UploadFileResultBean>) :
    MRequest<UploadFileResultBean>(0, URLProviderUtils.postUploadAvatar(), handler) {
}