package com.melodiousplayer.android.ui.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.PopupAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.DeleteMusicContract
import com.melodiousplayer.android.model.AudioBean
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.presenter.impl.DeleteMusicPresenterImpl
import com.melodiousplayer.android.service.AudioService
import com.melodiousplayer.android.service.IService
import com.melodiousplayer.android.util.StringUtil
import com.melodiousplayer.android.util.ThemeUtil
import com.melodiousplayer.android.widget.AudioOperationPopupWindow
import com.melodiousplayer.android.widget.LyricView
import com.melodiousplayer.android.widget.PlayListPopupWindow
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 音乐播放界面
 */
class AudioPlayerActivity : BaseActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener,
    AdapterView.OnItemClickListener, DeleteMusicContract.View {

    private lateinit var audioPlayerBackground: LinearLayout
    private lateinit var audioPlayerTop: RelativeLayout
    private lateinit var state: ImageView
    private lateinit var audioTitle: TextView
    private lateinit var artist: TextView
    private lateinit var audioAnimation: ImageView
    private lateinit var back: ImageView
    private lateinit var progress: TextView
    private lateinit var progressSeekBar: SeekBar
    private lateinit var mode: ImageView
    private lateinit var previous: ImageView
    private lateinit var next: ImageView
    private lateinit var playList: ImageView
    private lateinit var audioPlayerBottom: LinearLayout
    private lateinit var lyricView: LyricView
    private lateinit var more: ImageView
    private lateinit var playListPopupWindow: PopupWindow
    private lateinit var audioOperationPopupWindow: PopupWindow
    private lateinit var currentMusic: MusicBean
    private lateinit var audioServiceIntent: Intent
    private val connection by lazy { AudioConnection() }
    private var iService: IService? = null
    private var audioBean: AudioBean? = null
    private var drawable: AnimationDrawable? = null
    private var alpha: Float = 0f
    private var duration: Int = 0
    private var token: String? = null
    private val MSG_PROGRESS = 0
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_PROGRESS -> startUpdateProgress()
            }
        }
    }
    private val EDIT_MUSIC_REQUEST = 1
    private val deleteMusicPresenter = DeleteMusicPresenterImpl(this)

    override fun getLayoutId(): Int {
        return R.layout.activity_audio_player
    }

    override fun initData() {
        audioPlayerBackground = findViewById(R.id.music_player_background)
        audioPlayerTop = findViewById(R.id.music_player_top)
        state = findViewById(R.id.state)
        audioTitle = findViewById(R.id.audio_title)
        artist = findViewById(R.id.artist)
        audioAnimation = findViewById(R.id.audio_anim)
        back = findViewById(R.id.audio_back)
        progress = findViewById(R.id.progress)
        progressSeekBar = findViewById(R.id.progress_sk)
        mode = findViewById(R.id.mode)
        previous = findViewById(R.id.pre)
        next = findViewById(R.id.next)
        playList = findViewById(R.id.playlist)
        audioPlayerBottom = findViewById(R.id.audio_player_bottom)
        lyricView = findViewById(R.id.lyricView)
        more = findViewById(R.id.audio_more)
        // 记录当前窗体的透明度
        alpha = window.attributes.alpha
        val isDarkTheme = ThemeUtil.isDarkTheme(this)
        if (isDarkTheme) {
            val textColor = getColor(R.color.darkGray)
            audioPlayerBackground.setBackgroundResource(R.mipmap.music_night_bg)
            artist.setTextColor(textColor)
        }
        // 设置全屏
        setFullScreen(window)
        // 判断当前系统版本是否为Android 9.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        // 将音乐播放器顶部控件下移状态栏的位置
        ViewCompat.setOnApplyWindowInsetsListener(audioPlayerTop) { view, insets ->
            val params = view.layoutParams as LinearLayout.LayoutParams
            params.topMargin =
                insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            insets
        }
        // 通过AudioService播放音乐
        val isMyMusic = intent.getBooleanExtra("isMyMusic", false)
        if (isMyMusic) {
            more.visibility = View.VISIBLE
            token = intent.getStringExtra("token")
            val musicSerialized = intent.getSerializableExtra("music")
            if (musicSerialized != null) {
                currentMusic = musicSerialized as MusicBean
            }
        } else {
            more.visibility = View.GONE
        }
        // 注册EventBus
        EventBus.getDefault().register(this)
        audioServiceIntent = Intent()
        audioServiceIntent.putExtra("list", intent.getSerializableExtra("list"))
        audioServiceIntent.putExtra("position", intent.getIntExtra("position", 0))
        // 修改
        audioServiceIntent.setClass(this, AudioService::class.java)
        // 先绑定服务
        bindService(audioServiceIntent, connection, Context.BIND_AUTO_CREATE)
        // 再开启服务
        startService(audioServiceIntent)
    }

    /**
     * 设置全屏和沉浸式模式
     */
    fun setFullScreen(window: Window) {
        // 判断系统是否为Android 11及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //使用刘海屏也需要这一句进行全屏处理
            WindowCompat.setDecorFitsSystemWindows(window, false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(
                    WindowInsets.Type.navigationBars()
                            or WindowInsets.Type.displayCutout()
                )
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_DEFAULT
            }
            window.navigationBarColor = Color.TRANSPARENT
        } else {
            // 对于低于Android 10的系统，使用下面的方法或旧方式设置状态栏颜色
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
        // 设置状态栏透明
        window.statusBarColor = Color.TRANSPARENT
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
        // 更多操作按钮点击事件
        more.setOnClickListener(this)
        // 播放状态切换
        state.setOnClickListener(this)
        back.setOnClickListener { finish() }
        // 进度条变化监听
        progressSeekBar.setOnSeekBarChangeListener(this)
        // 播放模式点击事件
        mode.setOnClickListener(this)
        // 上一曲和下一曲点击事件
        previous.setOnClickListener(this)
        next.setOnClickListener(this)
        // 播放列表点击事件
        playList.setOnClickListener(this)
        // 歌词拖动进度更新监听
        lyricView.setProgressListener {
            // 更新播放进度
            iService?.seekTo(it)
            // 更新进度显示
            updateProgress(it)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.state -> updatePlayState()
            R.id.mode -> updatePlayMode()
            R.id.pre -> iService?.playPrevious()
            R.id.next -> iService?.playNext()
            R.id.playlist -> showPlayList()
            R.id.audio_more -> showAudioOperationPopupWindow()
            R.id.cancel -> audioOperationPopupWindow.dismiss()
            R.id.edit -> editMyMusic()
            R.id.delete -> {
                audioOperationPopupWindow.dismiss()
                AlertDialog.Builder(this).apply {
                    setTitle("提示")
                    setMessage("确定要删除音乐吗？")
                    setCancelable(false)
                    setPositiveButton("确定") { dialog, which ->
                        token?.let {
                            deleteMusicPresenter.deleteMusic(
                                it,
                                arrayOf(currentMusic.id!!)
                            )
                        }
                    }
                    setNegativeButton("取消") { dialog, which ->
                    }
                    show()
                }
            }
        }
    }

    /**
     * 从底部显示音乐操作PopupWindow
     */
    private fun showAudioOperationPopupWindow() {
        // 获取底部高度
        val bottomHeight = audioPlayerBottom.height
        audioOperationPopupWindow = AudioOperationPopupWindow(this, this, window)
        audioOperationPopupWindow.showAsDropDown(audioPlayerBottom, 0, bottomHeight)
    }

    /**
     * 修改我的音乐
     */
    private fun editMyMusic() {
        audioOperationPopupWindow.dismiss()
        // 进入添加音乐界面，传递音乐信息
        val intent = Intent(this, AddMusicActivity::class.java)
        intent.putExtra("isMyMusic", true)
        intent.putExtra("music", currentMusic)
        startActivityForResult(intent, EDIT_MUSIC_REQUEST)
    }

    /**
     * 显示播放列表
     */
    private fun showPlayList() {
        val list = iService?.getPlayList()
        list?.let {
            // 创建adapter
            val adapter = PopupAdapter(list)
            // 获取底部高度
            val bottomHeight = audioPlayerBottom.height
            playListPopupWindow = PlayListPopupWindow(this, adapter, this, window)
            playListPopupWindow.showAsDropDown(audioPlayerBottom, 0, bottomHeight)
        }
    }

    /**
     * 更新播放模式
     */
    private fun updatePlayMode() {
        // 修改Service中的mode
        iService?.updatePlayMode()
        // 修改界面模式图标
        updatePlayModeBtn()
    }

    /**
     * 根据播放模式修改播放模式图标
     */
    private fun updatePlayModeBtn() {
        iService?.let {
            // 获取播放模式
            val modeI: Int = it.getPlayMode()
            // 设置图标
            when (modeI) {
                AudioService.MODE_ALL -> mode.setImageResource(R.drawable.selector_btn_playmode_order)
                AudioService.MODE_SINGLE -> mode.setImageResource(R.drawable.selector_btn_playmode_single)
                AudioService.MODE_RANDOM -> mode.setImageResource(R.drawable.selector_btn_playmode_random)
            }
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
        if (itemBean.isOnline) {
            // 设置在线播放歌曲名称
            lyricView.setOnlineSongName(itemBean.lyric)
        } else {
            // 设置本地播放歌曲名称
            lyricView.setSongName(itemBean.displayName)
        }
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
        // 设置歌词播放总进度
        lyricView.setSongDuration(duration)
        // 进度条设置进度最大值
        progressSeekBar.max = duration
        // 更新播放进度
        startUpdateProgress()
        // 更新播放模式图标
        updatePlayModeBtn()
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
        handler.sendEmptyMessage(MSG_PROGRESS)
    }

    /**
     * 根据当前进度数据更新界面
     */
    private fun updateProgress(pro: Int) {
        // 更新进度数值
        progress.text = StringUtil.parseDuration(pro) + "/" + StringUtil.parseDuration(duration)
        // 更新进度条
        progressSeekBar.setProgress(pro)
        // 更新歌词播放进度
        lyricView.updateProgress(pro)
    }

    /**
     * 弹出的播放列表条目点击事件
     */
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // 播放当前的歌曲
        iService?.playPosition(position)
        playListPopupWindow.dismiss()
    }

    override fun onDeleteMusicSuccess(result: ResultBean) {
        myToast(getString(R.string.delete_music_success))
        // 关闭通知
        iService?.closeNotification()
        // 返回我的作品界面，传递用户信息，并退出AudioPlayerActivity
        val intent = Intent()
        intent.putExtra("user", currentMusic.sysUser)
        intent.putExtra("addOrModifyMusicSuccess", true)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDeleteMusicFailed(result: ResultBean) {
        myToast(getString(R.string.delete_music_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_MUSIC_REQUEST -> if (resultCode == RESULT_OK) {
                val addOrModifyMusicSuccess =
                    data?.getBooleanExtra("addOrModifyMusicSuccess", false)
                if (addOrModifyMusicSuccess == true) {
                    val intent = Intent()
                    intent.putExtra("addOrModifyMusicSuccess", true)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
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