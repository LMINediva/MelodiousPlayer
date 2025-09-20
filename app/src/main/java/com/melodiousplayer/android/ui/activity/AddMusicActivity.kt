package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.UploadLyricContract
import com.melodiousplayer.android.contract.UploadPosterContract
import com.melodiousplayer.android.contract.UploadThumbnailContract
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.presenter.impl.UploadLyricPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadMusicPosterPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadMusicThumbnailPresenterImpl
import com.melodiousplayer.android.util.StringUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

/**
 * 添加音乐界面
 */
class AddMusicActivity : BaseActivity(), ToolBarManager, View.OnClickListener,
    SeekBar.OnSeekBarChangeListener, UploadPosterContract.View, UploadThumbnailContract.View,
    UploadLyricContract.View {

    private lateinit var title: EditText
    private lateinit var artistName: EditText
    private lateinit var description: EditText
    private lateinit var posterPicture: ImageView
    private lateinit var posterPictureError: TextView
    private lateinit var thumbnailPictureError: TextView
    private lateinit var thumbnailPicture: ImageView
    private lateinit var lyric: ImageView
    private lateinit var lyricName: TextView
    private lateinit var lyricError: TextView
    private lateinit var music: ImageView
    private lateinit var musicName: TextView
    private lateinit var addMusic: Button
    private lateinit var progress: TextView
    private lateinit var progressSeekBar: SeekBar
    private lateinit var state: ImageView
    private lateinit var reset: ImageView
    private lateinit var token: String
    private lateinit var musicThumbnailUri: Uri
    private lateinit var musicPosterUri: Uri
    private lateinit var lyricUri: Uri
    private lateinit var lyricFileName: String
    private lateinit var musicUri: Uri
    private lateinit var musicFileName: String
    private val mediaPlayer = MediaPlayer()
    private val PERMISSION_REQUEST = 1
    private val ALBUM_POSTER_REQUEST = 1
    private val CROP_POSTER_REQUEST = 2
    private val ALBUM_THUMBNAIL_REQUEST = 3
    private val CROP_THUMBNAIL_REQUEST = 4
    private val LYRIC_FILE_REQUEST = 5
    private val MUSIC_FILE_REQUEST = 6
    private val MSG_PROGRESS = 0
    private val uploadMusicPosterPresenter = UploadMusicPosterPresenterImpl(this)
    private val uploadMusicThumbnailPresenter = UploadMusicThumbnailPresenterImpl(this)
    private val uploadLyricPresenter = UploadLyricPresenterImpl(this)
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_PROGRESS -> startUpdateProgress()
            }
        }
    }
    private var duration: Int = 0
    private var newMusicPoster: String? = null
    private var newMusicThumbnail: String? = null
    private var newLyric: String? = null
    private var allFilesUploadSuccess = true

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_music
    }

    override fun initData() {
        initAddMusicToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        title = findViewById(R.id.title)
        artistName = findViewById(R.id.artistName)
        description = findViewById(R.id.description)
        posterPicture = findViewById(R.id.posterPicture)
        posterPictureError = findViewById(R.id.posterPictureError)
        thumbnailPicture = findViewById(R.id.thumbnailPicture)
        thumbnailPictureError = findViewById(R.id.thumbnailPictureError)
        lyric = findViewById(R.id.lyric)
        lyricName = findViewById(R.id.lyricName)
        lyricError = findViewById(R.id.lyricError)
        music = findViewById(R.id.music)
        musicName = findViewById(R.id.musicName)
        addMusic = findViewById(R.id.addMusic)
        // 从SharedPreferences文件中读取token的值
        token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "").toString()
        requestPermissions()
    }

    override fun initListener() {
        posterPicture.setOnClickListener(this)
        thumbnailPicture.setOnClickListener(this)
        lyric.setOnClickListener(this)
        music.setOnClickListener(this)
        addMusic.setOnClickListener(this)
        lyricName.setOnClickListener(this)
        musicName.setOnClickListener(this)
    }

    /**
     * Toolbar上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.posterPicture -> {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // 通过setType方法限制类型为图像，否则有些android版本会同时显示视频
                intent.type = "image/*"
                startActivityForResult(intent, ALBUM_POSTER_REQUEST)
            }

            R.id.thumbnailPicture -> {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // 通过setType方法限制类型为图像，否则有些android版本会同时显示视频
                intent.type = "image/*"
                startActivityForResult(intent, ALBUM_THUMBNAIL_REQUEST)
            }

            R.id.lyric -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                // 过滤出文本文件，包括LRC文件
                intent.type = "text/*"
                startActivityForResult(intent, LYRIC_FILE_REQUEST)
            }

            R.id.lyricName -> {
                showScrollingDialog(this)
            }

            R.id.music -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                // 过滤出音频文件
                intent.type = "audio/*"
                startActivityForResult(intent, MUSIC_FILE_REQUEST)
            }

            R.id.musicName -> {
                showPlayMusicDialog(this)
            }

            R.id.state -> {
                updatePlayState()
            }

            R.id.reset -> {
                mediaPlayer.reset()
                // 停止更新进度
                handler.removeMessages(MSG_PROGRESS)
                // 重置进度数据
                updateProgress(0)
                state.setImageResource(R.drawable.selector_btn_audio_pause)
                initMediaPlayer(musicUri)
            }

            R.id.addMusic -> {
                val title = title.text.trim().toString()
                val artistName = artistName.text.trim().toString()
                val description = description.text.trim().toString()
                musicPosterUri.path?.let { File(it) }
                    ?.let { uploadMusicPosterPresenter.uploadPoster(token, it) }
                musicThumbnailUri.path?.let { File(it) }
                    ?.let { uploadMusicThumbnailPresenter.uploadThumbnail(token, it) }
                lyricUri.path?.let { File(it) }
                    ?.let { uploadLyricPresenter.uploadLyric(token, it) }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this, permissions,
                    PERMISSION_REQUEST
                )
            }
        }
    }

    /**
     * 权限请求结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST -> {
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        myToast("你拒绝了权限！")
                        break
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ALBUM_POSTER_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        if (checkPicture(uri, posterPictureError)) {
                            // 在截图界面显示选择好的照片
                            val intent = CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .getIntent(this)
                            startActivityForResult(intent, CROP_POSTER_REQUEST)
                        }
                    }
                }
            }

            CROP_POSTER_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = CropImage.getActivityResult(data).uri
                    // 将裁剪好的照片显示出来
                    if (resultUri != null) {
                        musicPosterUri = resultUri
                        Glide.with(this)
                            .load(resultUri)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(posterPicture)
                    }
                }
            }

            ALBUM_THUMBNAIL_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        if (checkPicture(uri, thumbnailPictureError)) {
                            // 在截图界面显示选择好的照片
                            val intent = CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .getIntent(this)
                            startActivityForResult(intent, CROP_THUMBNAIL_REQUEST)
                        }
                    }
                }
            }

            CROP_THUMBNAIL_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = CropImage.getActivityResult(data).uri
                    // 将裁剪好的照片显示出来
                    if (resultUri != null) {
                        musicThumbnailUri = resultUri
                        Glide.with(this)
                            .load(resultUri)
                            .skipMemoryCache(true)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(thumbnailPicture)
                    }
                }
            }

            LYRIC_FILE_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = data?.data
                    if (resultUri != null) {
                        if (checkLyricFile(resultUri)) {
                            lyricUri = resultUri
                            val cursor = contentResolver.query(
                                resultUri, null, null,
                                null, null
                            )
                            cursor?.use {
                                if (it.moveToFirst()) {
                                    val nameIndex =
                                        it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                                    lyricFileName = it.getString(nameIndex)
                                    lyric.visibility = View.GONE
                                    lyricName.visibility = View.VISIBLE
                                    lyricName.text = lyricFileName
                                }
                            }
                        }
                    }
                }
            }

            MUSIC_FILE_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = data?.data
                    if (resultUri != null) {
                        musicUri = resultUri
                        val cursor = contentResolver.query(
                            resultUri, null, null,
                            null, null
                        )
                        cursor?.use {
                            if (it.moveToFirst()) {
                                val nameIndex =
                                    it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                                musicFileName = it.getString(nameIndex)
                                music.visibility = View.GONE
                                musicName.visibility = View.VISIBLE
                                musicName.text = musicFileName
                                initMediaPlayer(resultUri)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查图片文件是否符合要求
     */
    private fun checkPicture(uri: Uri, textView: TextView): Boolean {
        // 获取照片的MIME类型
        val type = contentResolver.getType(uri)
        if (type == null) {
            // 无法获取MIME类型，可能文件路径不正确或文件不存在
            textView.text = "图片文件路径不正确或文件不存在！"
            textView.visibility = View.VISIBLE
            return false
        }
        // 检查是否是JPEG、JPG或PNG格式
        val isSupportedFormat = type == "image/jpeg" || type == "image/jpg" || type == "image/png"
        if (!isSupportedFormat) {
            // 不是支持格式
            textView.text = "图片只能是jpeg、jpg和png格式！"
            textView.visibility = View.VISIBLE
            return false
        }
        // 检查图片文件大小是否超过2MB（2 * 1024 * 1024字节）
        var fileSize: Long = 0
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                // 获取文件大小（字节）
                fileSize = inputStream.available().toLong()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            textView.text = "图片文件路径不正确或文件不存在！"
            textView.visibility = View.VISIBLE
            return false
        }
        val isLessThan2M = fileSize <= 2 * 1024 * 1024
        if (!isLessThan2M) {
            textView.text = "图片大小不能超过2MB！"
            textView.visibility = View.VISIBLE
            return false
        }
        return true
    }

    /**
     * 检查歌词文件是否符合要求
     */
    private fun checkLyricFile(uri: Uri): Boolean {
        // 获取文件扩展名
        val fileName = uri.lastPathSegment
        val fileExtension = fileName?.substringAfterLast(".")?.lowercase()
        // 检查是否为.lrc文件
        if (fileExtension != "lrc") {
            // 不是.lrc文件
            lyricError.text = "歌词文件只能是lrc格式！"
            lyricError.visibility = View.VISIBLE
            return false
        }
        // 检查歌词文件大小是否超过2MB（2 * 1024 * 1024字节）
        var fileSize: Long = 0
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                // 获取文件大小（字节）
                fileSize = inputStream.available().toLong()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            lyricError.text = "歌词文件路径不正确或文件不存在！"
            lyricError.visibility = View.VISIBLE
            return false
        }
        val isLessThan2M = fileSize <= 2 * 1024 * 1024
        if (!isLessThan2M) {
            lyricError.text = "歌词文件大小不能超过2MB！"
            lyricError.visibility = View.VISIBLE
            return false
        }
        return true
    }

    /**
     * 读取LRC文件内容
     */
    private fun readLrcFileContent(uri: Uri): String? {
        return try {
            contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 弹窗显示歌词
     */
    private fun showScrollingDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(lyricFileName)
            .setMessage(readLrcFileContent(lyricUri))
            .setCancelable(false)
            .setPositiveButton("确定") { dialog, which ->
            }
            .create()
        dialog.window?.setLayout(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        dialog.show()
    }

    /**
     * 弹窗显示音乐播放控件
     */
    private fun showPlayMusicDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(musicFileName)
            .setView(R.layout.popup_music_player)
            .setCancelable(false)
            .setPositiveButton("确定") { dialog, which ->
            }
            .create()
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
        progress = dialog.findViewById(R.id.progress)!!
        progressSeekBar = dialog.findViewById(R.id.progress_sk)!!
        state = dialog.findViewById(R.id.state)!!
        reset = dialog.findViewById(R.id.reset)!!
        // 播放状态切换
        state.setOnClickListener(this)
        reset.setOnClickListener(this)
        duration = mediaPlayer.duration
        // 进度条设置最大值
        progressSeekBar.max = duration
        // 进度条变化监听
        progressSeekBar.setOnSeekBarChangeListener(this)
    }

    /**
     * 初始化MediaPlayer
     */
    private fun initMediaPlayer(uri: Uri) {
        try {
            mediaPlayer.setDataSource(this, uri)
            mediaPlayer.prepare()
        } catch (e: Exception) {
            // 处理异常，例如文件不存在或格式不支持等
            e.printStackTrace()
        }
    }

    /**
     * 更新播放状态
     */
    private fun updatePlayState() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            state.setImageResource(R.drawable.selector_btn_audio_pause)
            // 停止更新进度
            handler.removeMessages(MSG_PROGRESS)
        } else {
            mediaPlayer.start()
            state.setImageResource(R.drawable.selector_btn_audio_play)
            // 更新播放进度
            startUpdateProgress()
        }
    }

    /**
     * 开始更新进度
     */
    private fun startUpdateProgress() {
        // 获取当前进度
        val progress: Int = mediaPlayer.currentPosition
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
        mediaPlayer.seekTo(progress)
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

    override fun onUploadPosterSuccess(result: UploadFileResultBean) {
        newMusicPoster = result.data?.title.toString()
    }

    override fun onUploadPosterFailed() {
        allFilesUploadSuccess = false
        posterPictureError.text = "海报图片上传失败！"
        posterPictureError.visibility = View.VISIBLE
    }

    override fun onUploadThumbnailSuccess(result: UploadFileResultBean) {
        newMusicThumbnail = result.data?.title.toString()
    }

    override fun onUploadThumbnailFailed() {
        allFilesUploadSuccess = false
        thumbnailPictureError.text = "海报缩略图上传失败！"
        thumbnailPictureError.visibility = View.VISIBLE
    }

    override fun onUploadLyricSuccess(result: UploadFileResultBean) {
        newLyric = result.data?.title.toString()
    }

    override fun onUploadLyricFailed() {
        allFilesUploadSuccess = false
        lyricError.text = "歌词文件上传失败！"
        lyricError.visibility = View.VISIBLE
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
        // 清空handler发送的所有消息
        handler.removeCallbacksAndMessages(null)
    }

}