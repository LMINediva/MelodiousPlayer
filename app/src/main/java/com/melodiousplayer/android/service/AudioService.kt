package com.melodiousplayer.android.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import com.melodiousplayer.android.model.AudioBean
import org.greenrobot.eventbus.EventBus
import java.util.Random

class AudioService : Service() {

    var list: ArrayList<AudioBean>? = null

    // 正在播放的position
    var position: Int = -2
    var mediaPlayer: MediaPlayer? = null
    val binder by lazy { AudioBinder() }

    companion object {
        val MODE_ALL = 1
        val MODE_SINGLE = 2
        val MODE_RANDOM = 3
    }

    val sp by lazy { getSharedPreferences("config", Context.MODE_PRIVATE) }

    // 播放模式
    var mode = MODE_ALL

    override fun onCreate() {
        super.onCreate()
        // 获取播放模式
        mode = sp.getInt("mode", 1)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 想要播放的position
        val pos = intent?.getIntExtra("position", -1) ?: -1
        if (pos != position) {
            position = pos
            // 想要播放的条目和正在播放的条目不是同一首
            // 获取list以及position
            list = intent?.getParcelableArrayListExtra<AudioBean>("list")
            // 开始播放音乐
            binder.playItem()
        } else {
            // 主动通知界面更新
            binder.notifyUpdateUI()
        }
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
        fun notifyUpdateUI() {
            // 发送端
            EventBus.getDefault().post(list?.get(position))
        }

        fun playItem() {
            // 如果MediaPlayer已经存在，就先释放掉
            if (mediaPlayer != null) {
                mediaPlayer?.reset()
                mediaPlayer?.release()
                mediaPlayer = null
            }
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
            // 自动播放下一曲
            autoPlayNext()
        }

        /**
         * 根据播放模式自动播放下一曲
         */
        private fun autoPlayNext() {
            when (mode) {
                MODE_ALL -> {
                    list?.let { position = (position + 1) % it.size }
                }

                MODE_RANDOM -> {
                    list?.let { position = Random().nextInt(it.size) }
                }
            }
            playItem()
        }

        /**
         * 修改播放模式
         * MODE_ALL MODE_SINGLE MODE_RANDOM
         */
        override fun updatePlayMode() {
            when (mode) {
                MODE_ALL -> mode = MODE_SINGLE
                MODE_SINGLE -> mode = MODE_RANDOM
                MODE_RANDOM -> mode = MODE_ALL
            }
            // 保存播放模式
            sp.edit().putInt("mode", mode).apply()
        }

        /**
         * 获取播放模式
         */
        override fun getPlayMode(): Int {
            return mode
        }

        /**
         * 播放上一曲
         */
        override fun playPrevious() {
            list?.let {
                // 获取要播放歌曲的position
                when (mode) {
                    MODE_RANDOM -> position = Random().nextInt(it.size - 1)
                    else -> {
                        if (position == 0) {
                            position = it.size - 1
                        } else {
                            position--
                        }
                    }
                }
                // 调用playItem方法进行播放
                playItem()
            }
        }

        /**
         * 播放下一曲
         */
        override fun playNext() {
            list?.let {
                when (mode) {
                    MODE_RANDOM -> position = Random().nextInt(it.size - 1)
                    else -> position = (position + 1) % it.size
                }
            }
            playItem()
        }

    }

}