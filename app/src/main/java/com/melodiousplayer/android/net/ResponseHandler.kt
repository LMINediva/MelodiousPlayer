package com.melodiousplayer.android.net

/**
 * 请求回调
 */
interface ResponseHandler<RESPONSE> {
    fun onError(msg: String?)
    fun onSuccess(result: RESPONSE)
}