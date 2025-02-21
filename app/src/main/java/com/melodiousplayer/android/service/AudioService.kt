package com.melodiousplayer.android.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.melodiousplayer.android.R
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.ui.activity.AudioPlayerActivity
import com.melodiousplayer.android.ui.activity.MainActivity
import org.greenrobot.eventbus.EventBus
import java.util.Random

class AudioService : Service() {

    var list: ArrayList<AudioBean>? = null
    var manager: NotificationManager? = null
    var notification: Notification? = null

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

    val FROM_PRE = 1
    val FROM_NEXT = 2
    val FROM_STATE = 3
    val FROM_CONTENT = 4

    override fun onCreate() {
        super.onCreate()
        // 获取播放模式
        mode = sp.getInt("mode", 1)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // 判断进入Service的方法
        val from = intent?.getIntExtra("from", -1)
        when (from) {
            FROM_PRE -> {
                binder.playPrevious()
            }

            FROM_NEXT -> {
                binder.playNext()
            }

            FROM_CONTENT -> {
                binder.notifyUpdateUI()
            }

            FROM_STATE -> {
                binder.updatePlayState()
            }

            else -> {
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
            }
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
            start()
            // 通知界面更新
            notifyUpdateUI()
            // 显示通知
            showNotification()
        }

        /**
         * 显示通知
         */
        private fun showNotification() {
            manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        "normal",
                        "音乐播放后台服务",
                        NotificationManager.IMPORTANCE_DEFAULT
                    )
                // 禁用震动
                channel.enableVibration(false)
                // 设置null来移除铃声
                channel.setSound(null, null)
                manager?.createNotificationChannel(channel)
            }
            notification = getNotification()
            manager?.notify(1, notification)
            // 启动前台服务并显示通知
            startForeground(1, notification)
        }

        /**
         * 创建Notification
         */
        private fun getNotification(): Notification? {
            val notification = NotificationCompat.Builder(this@AudioService, "normal")
                .setTicker("正在播放歌曲${list?.get(position)?.displayName}")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setCustomContentView(getRemoteViews()) // 自定义通知view
                .setWhen(System.currentTimeMillis())
                .setOngoing(true) // 设置不能滑动删除通知
                .setContentIntent(getPendingIntent()) // 通知栏主体点击事件
                .build()
            return notification
        }

        /**
         * 创建通知自定义view
         */
        private fun getRemoteViews(): RemoteViews? {
            val remoteViews = RemoteViews(packageName, R.layout.notification)
            // 修改标题和内容
            remoteViews.setTextViewText(R.id.title, list?.get(position)?.displayName)
            remoteViews.setTextViewText(R.id.artist, list?.get(position)?.artist)
            // 处理上一曲、下一曲和状态点击事件
            remoteViews.setOnClickPendingIntent(R.id.pre, getPrePendingIntent())
            remoteViews.setOnClickPendingIntent(R.id.state, getStatePendingIntent())
            remoteViews.setOnClickPendingIntent(R.id.next, getNextPendingIntent())
            return remoteViews
        }

        /**
         * 下一曲点击事件
         */
        private fun getNextPendingIntent(): PendingIntent? {
            // 点击主体进入音乐播放服务中
            val intent = Intent(this@AudioService, AudioService::class.java)
            intent.putExtra("from", FROM_NEXT)
            val pendingIntent = PendingIntent.getService(
                this@AudioService, 2, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            return pendingIntent
        }

        /**
         * 播放暂停按钮点击事件
         */
        private fun getStatePendingIntent(): PendingIntent? {
            // 点击主体进入音乐播放服务中
            val intent = Intent(this@AudioService, AudioService::class.java)
            intent.putExtra("from", FROM_STATE)
            val pendingIntent = PendingIntent.getService(
                this@AudioService, 3, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            return pendingIntent
        }

        /**
         * 上一曲点击事件
         */
        private fun getPrePendingIntent(): PendingIntent? {
            // 点击主体进入音乐播放服务中
            val intent = Intent(this@AudioService, AudioService::class.java)
            intent.putExtra("from", FROM_PRE)
            val pendingIntent = PendingIntent.getService(
                this@AudioService, 4, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            return pendingIntent
        }

        /**
         * 通知栏主体点击事件
         */
        private fun getPendingIntent(): PendingIntent? {
            // 点击主体进入音乐播放界面中
            val intentM = Intent(this@AudioService, MainActivity::class.java)
            val intentA = Intent(this@AudioService, AudioPlayerActivity::class.java)
            intentA.putExtra("from", FROM_CONTENT)
            val intents = arrayOf(intentM, intentA)
            val pendingIntent = PendingIntent.getActivities(
                this@AudioService, 1, intents,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            return pendingIntent
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
                    pause()
                } else {
                    // 暂停，播放
                    start()
                }
            }
        }

        /**
         * 暂停
         */
        private fun pause() {
            mediaPlayer?.pause()
            EventBus.getDefault().post(list?.get(position))
            // 更新图标
            notification?.contentView?.setImageViewResource(
                R.id.state,
                R.drawable.selector_btn_audio_pause
            )
            // 重新显示
            manager?.notify(1, notification)
        }

        /**
         * 开始
         */
        private fun start() {
            mediaPlayer?.start()
            EventBus.getDefault().post(list?.get(position))
            // 更新图标
            notification?.contentView?.setImageViewResource(
                R.id.state,
                R.drawable.selector_btn_audio_play
            )
            // 重新显示
            manager?.notify(1, notification)
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

        /**
         * 获取播放集合
         */
        override fun getPlayList(): List<AudioBean>? {
            return list
        }

        /**
         * 播放当前位置的歌曲
         */
        override fun playPosition(position: Int) {
            this@AudioService.position = position
            playItem()
        }

    }

}