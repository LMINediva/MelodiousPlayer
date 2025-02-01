package com.melodiousplayer.android.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.service.AudioService
import com.melodiousplayer.android.service.IService
import org.greenrobot.eventbus.EventBus

/**
 * 音乐播放界面
 */
class AudioPlayerActivity : BaseActivity(), View.OnClickListener {

    val connection by lazy { AudioConnection() }
    var iService: IService? = null
    private lateinit var state: ImageView
    private lateinit var audioTitle: TextView

    override fun getLayoutId(): Int {
        return R.layout.activity_audio_player
    }

    override fun initData() {
        state = findViewById(R.id.state)
        audioTitle = findViewById(R.id.audio_title)
        // 注册EventBus
        EventBus.getDefault().register(this)
        // 通过AudioService播放音乐
        val intent = intent
        // 修改
        intent.setClass(this, AudioService::class.java)
        // 先开启服务
        startService(intent)
        // 再绑定服务
        bindService(intent, connection, Context.BIND_AUTO_CREATE)
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
        when (v?.id) {
            R.id.state -> updatePlayState()
        }
    }

    /**
     * 更新播放状态
     */
    private fun updatePlayState() {
        // 更新播放状态
        iService?.updatePlayState()
        // 更新播放状态图标
        updatePlayStateBtn()
    }

    /**
     * 根据播放状态更新图标
     */
    private fun updatePlayStateBtn() {
        // 获取当前播放状态
        val isPlaying = iService?.isPlaying()
        isPlaying?.let {
            // 根据状态更新图标
            if (isPlaying) {
                // 播放
                state.setImageResource(R.drawable.selector_btn_audio_play)
            } else {
                // 暂停
                state.setImageResource(R.drawable.selector_btn_audio_pause)
            }
        }
    }

    /**
     * 接收EventBus方法
     */
    fun onEventMainThread(itemBean: AudioBean) {
        // 歌曲名
        audioTitle.text = itemBean.displayName
    }

    override fun onDestroy() {
        super.onDestroy()
        // 手动解绑服务
        unbindService(connection)
        // 反注册EventBus
        EventBus.getDefault().unregister(this)
    }

}