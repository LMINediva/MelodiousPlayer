package com.melodiousplayer.android.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.service.AudioService
import com.melodiousplayer.android.service.IService

/**
 * 音乐播放界面
 */
class AudioPlayerActivity : BaseActivity(), View.OnClickListener {

    val connection by lazy { AudioConnection() }
    var iService: IService? = null
    private lateinit var state: ImageView

    override fun getLayoutId(): Int {
        return R.layout.activity_audio_player
    }

    override fun initData() {
        state = findViewById(R.id.state)
        // 通过AudioService播放音乐
        val intent = intent
        // 修改
        intent.setClass(this, AudioService::class.java)
        // 先开启服务
        startService(intent)
        // 再绑定服务
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
        // 播放音乐
    }

    inner class AudioConnection : ServiceConnection {

        /**
         * Service连接时
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iService = service as IService
        }

        /**
         * Service意外断开连接时
         */
        override fun onServiceDisconnected(name: ComponentName?) {

        }

    }

    override fun initListener() {
        // 播放状态切换
        state.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        
    }

    override fun onDestroy() {
        super.onDestroy()
        // 手动解绑服务
        unbindService(connection)
    }

}