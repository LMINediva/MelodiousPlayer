package com.melodiousplayer.android.net

import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.util.URLProviderUtils

/**
 * 上传用户反馈图片网络请求
 */
class UploadFeedBackPictureRequest(handler: ResponseHandler<UploadFileResultBean>) :
    MRequest<UploadFileResultBean>(0, URLProviderUtils.postUploadFeedBackPicture(), handler) {
}