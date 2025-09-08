package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.UpdateImageResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 更新头像网络请求
 */
class UpdateAvatarRequest(handler: ResponseHandler<UpdateImageResultBean>) :
    MRequest<UpdateImageResultBean>(0, URLProviderUtils.postUpdateAvatar(), handler) {
}