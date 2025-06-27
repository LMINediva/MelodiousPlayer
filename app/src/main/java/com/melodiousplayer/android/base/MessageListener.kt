package com.melodiousplayer.android.base

/**
 * Fragment向Activity回调消息接口
 */
interface MessageListener {

    fun onMessageReceived(message: String)

}