package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 上传悦单缩略图图片网络请求
 */
class UploadMusicListThumbnailRequest(handler: ResponseHandler<UploadFileResultBean>) :
    MRequest<UploadFileResultBean>(0, URLProviderUtils.postUploadMusicListPicture(), handler) {
}