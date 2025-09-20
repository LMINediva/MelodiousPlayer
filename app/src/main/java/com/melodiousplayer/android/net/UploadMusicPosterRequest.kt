package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 上传音乐海报图片网络请求
 */
class UploadMusicPosterRequest(handler: ResponseHandler<UploadFileResultBean>) :
    MRequest<UploadFileResultBean>(0, URLProviderUtils.postUploadMusicPicture(), handler) {
}