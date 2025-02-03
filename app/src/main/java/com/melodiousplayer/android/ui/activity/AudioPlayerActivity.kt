package com.melodiousplayer.android.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.service.AudioService
import com.melodiousplayer.android.service.IService
import com.melodiousplayer.android.util.StringUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 音乐播放界面
 */
class AudioPlayerActivity : BaseActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    val connection by lazy { AudioConnection() }
    var iService: IService? = null
    var audioBean: AudioBean? = null
    var drawable: AnimationDrawable? = null
    var duration: Int = 0
    val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_PROGRESS -> startUpdateProgress()
            }
        }
    }
    val MSG_PROGRESS = 0
    private lateinit var state: ImageView
    private lateinit var audioTitle: TextView
    private lateinit var artist: TextView
    private lateinit var audioAnimation: ImageView
    private lateinit var back: ImageView
    private lateinit var progress: TextView
    private lateinit var progressSeekBar: SeekBar

    override fun getLayoutId(): Int {
        return R.layout.activity_audio_player
    }

    override fun initData() {
        state = findViewById(R.id.state)
        audioTitle = findViewById(R.id.audio_title)
        artist = findViewById(R.id.artist)
        audioAnimation = findViewById(R.id.audio_anim)
        back = findViewById(R.id.audio_back)
        progress = findViewById(R.id.progress)
        progressSeekBar = findViewById(R.id.progress_sk)
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
        back.setOnClickListener { finish() }
        // 进度条变化监听
        progressSeekBar.setOnSeekBarChangeListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.state -> updatePlayState()
        }
    }

    /**
     * 进度改变回调
     * progress：改变之后的进度
     * fromUser：true代表通过用户手指拖动改变进度，false代表通过代码方式改变进度
     */
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        // 判断是否是用户操作
        if (!fromUser) return
        // 更新播放进度
        iService?.seekTo(progress)
        // 更新界面进度显示
        updateProgress(progress)
    }

    /**
     * 手指触摸SeekBar回调
     */
    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    /**
     * 手指离开SeekBar回调
     */
    override fun onStopTrackingTouch(seekBar: SeekBar?) {

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
                // 开始播放动画
                drawable?.start()
                // 开始更新进度
                handler.sendEmptyMessage(MSG_PROGRESS)
            } else {
                // 暂停
                state.setImageResource(R.drawable.selector_btn_audio_pause)
                // 停止播放动画
                drawable?.stop()
                // 停止更新进度
                handler.removeMessages(MSG_PROGRESS)
            }
        }
    }

    /**
     * 接收EventBus方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(itemBean: AudioBean) {
        // 记录播放歌曲的bean
        this.audioBean = itemBean
        // 歌曲名
        audioTitle.text = itemBean.displayName
        // 歌手名
        artist.text = itemBean.artist
        // 更新播放状态按钮
        updatePlayStateBtn()
        // 动画播放
        drawable = audioAnimation.drawable as AnimationDrawable
        drawable?.start()
        // 获取总进度
        duration = iService?.getDuration() ?: 0
        // 进度条设置进度最大值
        progressSeekBar.max = duration
        // 更新播放进度
        startUpdateProgress()
    }

    /**
     * 开始更新进度
     */
    private fun startUpdateProgress() {
        // 获取当前进度
        val progress: Int = iService?.getProgress() ?: 0
        // 更新进度数据
        updateProgress(progress)
        // 定时获取进度
        handler.sendEmptyMessageDelayed(MSG_PROGRESS, 1000)
    }

    /**
     * 根据当前进度数据更新界面
     */
    private fun updateProgress(pro: Int) {
        // 更新进度数值
        progress.text = StringUtil.parseDuration(pro) + "/" + StringUtil.parseDuration(duration)
        // 更新进度条
        progressSeekBar.setProgress(pro)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 手动解绑服务
        unbindService(connection)
        // 反注册EventBus
        EventBus.getDefault().unregister(this)
        // 清空handler发送的所有消息
        handler.removeCallbacksAndMessages(null)
    }

}