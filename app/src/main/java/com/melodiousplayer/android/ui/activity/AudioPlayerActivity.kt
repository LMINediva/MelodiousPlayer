package com.melodiousplayer.android.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.media.MediaPlayer
import android.os.IBinder
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.service.AudioService

/**
 * 音乐播放界面
 */
class AudioPlayerActivity : BaseActivity() {

    val connection by lazy { AudioConnection() }

    override fun getLayoutId(): Int {
        return R.layout.activity_audio_player
    }

    override fun initData() {
        val list = intent.getParcelableArrayListExtra<AudioBean>("list")
        val position = intent.getIntExtra("position", -1)
        // 通过AudioService播放音乐
        val intent = Intent(this, AudioService::class.java)
        // 通过intent将list以及position传递过去
        intent.putExtra("list", list)
        intent.putExtra("position", position)
        // 先开启服务
        startService(intent)
        // 再绑定服务
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        // 播放音乐
    }

    class AudioConnection : ServiceConnection {

        /**
         * Service连接时
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

        }

        /**
         * Service意外断开连接时
         */
        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

}