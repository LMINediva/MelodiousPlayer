package com.melodiousplayer.android.service

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.melodiousplayer.android.model.AudioBean
import org.greenrobot.eventbus.EventBus

class AudioService : Service() {

    var list: ArrayList<AudioBean>? = null
    var position: Int = 0
    var mediaPlayer: MediaPlayer? = null
    val binder by lazy { AudioBinder() }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 获取list以及position
        list = intent?.getParcelableArrayListExtra<AudioBean>("list")
        position = intent?.getIntExtra("position", -1) ?: -1
        // 开始播放音乐
        binder.playItem()
        // START_STICKY 粘性的 Service被强制杀死之后，会尝试重新启动Service，不会传递原来的Intent(null)
        // START_NOT_STICKY 非粘性的 Service被强制杀死之后，不会尝试重新启动Service
        // START_REDELIVER_INTENT Service被强制杀死之后，会尝试重新启动Service，会传递原来的Intent
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    inner class AudioBinder : Binder(), IService, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener {

        override fun onPrepared(mp: MediaPlayer?) {
            // 播放音乐
            mediaPlayer?.start()
            // 通知界面更新
            notifyUpdateUI()
        }

        /**
         * 通知界面更新
         */
        private fun notifyUpdateUI() {
            // 发送端
            EventBus.getDefault().post(list?.get(position))
        }

        fun playItem() {
            mediaPlayer = MediaPlayer()
            mediaPlayer?.let {
                it.setOnPreparedListener(this)
                it.setOnCompletionListener(this)
                it.setDataSource(list?.get(position)?.data)
                it.prepareAsync()
            }
        }

        /**
         * 更新播放状态
         */
        override fun updatePlayState() {
            // 获取当前播放状态
            val isPlaying = isPlaying()
            // 切换播放状态
            isPlaying?.let {
                if (isPlaying) {
                    // 播放，暂停
                    mediaPlayer?.pause()
                } else {
                    // 暂停，播放
                    mediaPlayer?.start()
                }
            }
        }

        override fun isPlaying(): Boolean? {
            return mediaPlayer?.isPlaying
        }

        /**
         * 获取总进度
         */
        override fun getDuration(): Int {
            return mediaPlayer?.duration ?: 0
        }

        /**
         * 获取当前播放进度
         */
        override fun getProgress(): Int {
            return mediaPlayer?.currentPosition ?: 0
        }

        /**
         * 跳转到当前进度播放
         */
        override fun seekTo(progress: Int) {
            mediaPlayer?.seekTo(progress)
        }

        /**
         * 歌曲播放完成之后回调
         */
        override fun onCompletion(mp: MediaPlayer?) {

        }

    }

}