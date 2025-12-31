package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Message
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.AddMusicContract
import com.melodiousplayer.android.contract.DeleteUploadMusicFileCacheContract
import com.melodiousplayer.android.contract.GetLyricTextContract
import com.melodiousplayer.android.contract.TitleContract
import com.melodiousplayer.android.contract.UploadLyricContract
import com.melodiousplayer.android.contract.UploadMusicContract
import com.melodiousplayer.android.contract.UploadPosterContract
import com.melodiousplayer.android.contract.UploadThumbnailContract
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.presenter.impl.AddMusicPresenterImpl
import com.melodiousplayer.android.presenter.impl.DeleteUploadMusicFileCachePresenterImpl
import com.melodiousplayer.android.presenter.impl.GetLyricTextPresenterImpl
import com.melodiousplayer.android.presenter.impl.TitlePresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadLyricPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadMusicPosterPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadMusicPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadMusicThumbnailPresenterImpl
import com.melodiousplayer.android.util.StringUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import com.melodiousplayer.android.util.UnitUtil
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.InputStream
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * 添加音乐界面
 */
class AddMusicActivity : BaseActivity(), ToolBarManager, View.OnClickListener,
    SeekBar.OnSeekBarChangeListener, UploadPosterContract.View, UploadThumbnailContract.View,
    UploadLyricContract.View, UploadMusicContract.View, AddMusicContract.View,
    GetLyricTextContract.View, DeleteUploadMusicFileCacheContract.View, TitleContract.View {

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
    private lateinit var musicError: TextView
    private lateinit var addMusic: Button
    private lateinit var editMusic: Button
    private lateinit var progress: TextView
    private lateinit var progressSeekBar: SeekBar
    private lateinit var state: ImageView
    private lateinit var reset: ImageView
    private lateinit var progressInfo: LinearLayout
    private lateinit var uploadCompletedText: TextView
    private lateinit var progressText: TextView
    private lateinit var currentText: TextView
    private lateinit var totalText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var hintInfo: TextView
    private lateinit var token: String
    private lateinit var currentUser: UserBean
    private lateinit var currentMusic: MusicBean
    private lateinit var lyricResult: ResultBean
    private lateinit var musicPath: String
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
    private val uploadMusicPresenter = UploadMusicPresenterImpl(this)
    private val addMusicPresenter = AddMusicPresenterImpl(this)
    private val getLyricTextPresenter = GetLyricTextPresenterImpl(this)
    private val deleteUploadMusicFileCachePresenter = DeleteUploadMusicFileCachePresenterImpl(this)
    private val titlePresenter = TitlePresenterImpl(this)
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
    private var newMusic: String? = null
    private var musicType: String? = null
    private var musicSize: Float = 0F
    private var hdMusicSize: Float = 0F
    private var uhdMusicSize: Float = 0F
    private var isAddMusicSuccess: Boolean = false
    private var isMyMusic: Boolean = false

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_music
    }

    override fun initData() {
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
        musicError = findViewById(R.id.musicError)
        addMusic = findViewById(R.id.addMusic)
        editMusic = findViewById(R.id.editMusic)
        isMyMusic = intent.getBooleanExtra("isMyMusic", false)
        if (isMyMusic) {
            initEditMusic()
        } else {
            addMusic.visibility = View.VISIBLE
            editMusic.visibility = View.GONE
            initAddMusicToolBar()
        }
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
        }
        // 从SharedPreferences文件中读取token的值
        token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "").toString()
        musicPath =
            URLProviderUtils.protocol + URLProviderUtils.serverAddress + URLProviderUtils.musicPath
        requestPermissions()
    }

    /**
     * 初始化修改音乐界面
     */
    private fun initEditMusic() {
        addMusic.visibility = View.GONE
        editMusic.visibility = View.VISIBLE
        initEditMusicToolBar()
        val musicSerialized = intent.getSerializableExtra("music")
        if (musicSerialized != null) {
            currentMusic = musicSerialized as MusicBean
            currentUser = currentMusic.sysUser!!
        }
        title.setText(currentMusic.title)
        artistName.setText(currentMusic.artistName)
        description.setText(currentMusic.description)
        if (!currentMusic.posterPic.isNullOrBlank()) {
            Glide.with(this)
                .load(
                    URLProviderUtils.protocol + URLProviderUtils.serverAddress
                            + URLProviderUtils.musicImagePath + currentMusic.posterPic
                )
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(posterPicture)
        }
        if (!currentMusic.thumbnailPic.isNullOrBlank()) {
            Glide.with(this)
                .load(
                    URLProviderUtils.protocol + URLProviderUtils.serverAddress
                            + URLProviderUtils.musicImagePath + currentMusic.thumbnailPic
                )
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(thumbnailPicture)
        }
        if (!currentMusic.lyric.isNullOrBlank()) {
            lyricName.visibility = View.VISIBLE
            lyricName.text = currentMusic.lyric
            getLyricTextPresenter.getLyricText(currentMusic.lyric!!)
        }
        if (!currentMusic.url.isNullOrBlank()) {
            musicName.visibility = View.VISIBLE
            musicName.text = currentMusic.url
        }
    }

    override fun initListener() {
        posterPicture.setOnClickListener(this)
        thumbnailPicture.setOnClickListener(this)
        lyric.setOnClickListener(this)
        music.setOnClickListener(this)
        addMusic.setOnClickListener(this)
        editMusic.setOnClickListener(this)
        lyricName.setOnClickListener(this)
        musicName.setOnClickListener(this)
    }

    /**
     * Toolbar上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                deleteUploadMusicFileCache()
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
                initMediaPlayer(musicPath + newMusic)
                mediaPlayer.setOnPreparedListener {
                    duration = mediaPlayer.duration
                    // 进度条设置最大值
                    progressSeekBar.max = duration
                }
            }

            R.id.addMusic -> {
                if (token.isNotEmpty()) {
                    val musicTitle = title.text.trim().toString()
                    val music = MusicBean(
                        null, null, musicTitle, null, null,
                        null, null, null, null, null,
                        null, null, null, null,
                        0, null
                    )
                    val json = Gson().toJson(music)
                    titlePresenter.checkTitle(
                        token,
                        URLProviderUtils.postCheckMusicTitle(),
                        musicTitle,
                        json
                    )
                }
            }

            R.id.editMusic -> {
                if (token.isNotEmpty()) {
                    val musicTitle = title.text.trim().toString()
                    val artistName = artistName.text.trim().toString()
                    val description = description.text.trim().toString()
                    currentMusic.title = musicTitle
                    currentMusic.artistName = artistName
                    currentMusic.description = description
                    if (!newMusicPoster.isNullOrBlank()) {
                        currentMusic.posterPic = newMusicPoster
                    }
                    if (!newMusicThumbnail.isNullOrBlank()) {
                        currentMusic.thumbnailPic = newMusicThumbnail
                    }
                    if (!newLyric.isNullOrBlank()) {
                        currentMusic.lyric = newLyric
                    }
                    if (!newMusic.isNullOrBlank()) {
                        currentMusic.type = musicType
                        currentMusic.url = newMusic
                        currentMusic.hdUrl = newMusic
                        currentMusic.uhdUrl = newMusic
                        currentMusic.musicSize = musicSize
                        currentMusic.hdMusicSize = musicSize
                        currentMusic.uhdMusicSize = musicSize
                    }
                    addMusicPresenter.addMusic(token, currentMusic)
                }
            }
        }
    }

    /**
     * 请求权限
     */
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
                                .setInitialCropWindowPaddingRatio(0F)
                                .getIntent(this)
                            startActivityForResult(intent, CROP_POSTER_REQUEST)
                        }
                    }
                }
            }

            CROP_POSTER_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = CropImage.getActivityResult(data).uri
                    // 将裁剪好的音乐海报图片上传到服务器
                    if (resultUri != null) {
                        if (token.isNotEmpty()) {
                            showUploadFileDialog(this)
                            resultUri.path?.let { File(it) }
                                ?.let { uploadMusicPosterPresenter.uploadPoster(token, it) }
                        }
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
                                .setInitialCropWindowPaddingRatio(0F)
                                .getIntent(this)
                            startActivityForResult(intent, CROP_THUMBNAIL_REQUEST)
                        }
                    }
                }
            }

            CROP_THUMBNAIL_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = CropImage.getActivityResult(data).uri
                    // 将裁剪好的音乐缩略图图片上传到服务器
                    if (resultUri != null) {
                        if (token.isNotEmpty()) {
                            showUploadFileDialog(this)
                            resultUri.path?.let { File(it) }
                                ?.let { uploadMusicThumbnailPresenter.uploadThumbnail(token, it) }
                        }
                    }
                }
            }

            LYRIC_FILE_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = data?.data
                    if (resultUri != null) {
                        if (checkLyricFile(resultUri)) {
                            if (token.isNotEmpty()) {
                                val file = getFileFromUri(resultUri)
                                if (file != null) {
                                    showUploadFileDialog(this)
                                    uploadLyricPresenter.uploadLyric(token, file)
                                } else {
                                    lyricError.text = "歌词文件路径不正确或文件已损坏！"
                                    lyricError.visibility = View.VISIBLE
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
                        if (checkMusic(resultUri)) {
                            if (token.isNotEmpty()) {
                                val file = getFileFromUri(resultUri)
                                if (file != null) {
                                    showUploadFileDialog(this)
                                    uploadMusicPresenter.uploadMusic(token, file)
                                } else {
                                    musicError.text = "音乐文件路径不正确或文件已损坏！"
                                    musicError.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 从Uri中获取File
     */
    fun getFileFromUri(uri: Uri): File? {
        try {
            val returnCursor: Cursor? =
                contentResolver.query(uri, null, null, null, null)
            val nameIndex: Int = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            returnCursor.moveToFirst()
            val name: String = returnCursor.getString(nameIndex)
            val file = File(filesDir, name)
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable: Int = inputStream!!.available()
            val bufferSize = Math.min(bytesAvailable, maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream!!.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            returnCursor.close()
            inputStream.close()
            outputStream.close()
            return File(file.path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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
     * 检查音乐文件是否符合要求
     */
    private fun checkMusic(uri: Uri): Boolean {
        // 获取音乐文件的MIME类型
        val type = contentResolver.getType(uri)
        if (type == null) {
            // 无法获取MIME类型，可能文件路径不正确或文件不存在
            musicError.text = "音乐文件路径不正确或文件不存在！"
            musicError.visibility = View.VISIBLE
            return false
        }
        // 检查是否是MP3或WAV格式
        val isSupportedFormat = type == "audio/mpeg" || type == "audio/wav"
        if (!isSupportedFormat) {
            // 不是支持格式
            musicError.text = "音乐文件只能是mp3和wav格式！"
            musicError.visibility = View.VISIBLE
            return false
        }
        // 检查图片文件大小是否超过50MB（50 * 1024 * 1024字节）
        var fileSize: Long = 0
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                // 获取文件大小（字节）
                fileSize = inputStream.available().toLong()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            musicError.text = "音乐文件路径不正确或文件不存在！"
            musicError.visibility = View.VISIBLE
            return false
        }
        val isLessThan50M = fileSize <= 50 * 1024 * 1024
        if (!isLessThan50M) {
            musicError.text = "音乐文件大小不能超过50MB！"
            musicError.visibility = View.VISIBLE
            return false
        }
        val bigDecimal = BigDecimal(fileSize / (1024 * 1024))
        val fileSizeMB = bigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
        musicSize = fileSizeMB
        hdMusicSize = fileSizeMB
        uhdMusicSize = fileSizeMB
        musicType = type
        return true
    }

    /**
     * 弹窗显示歌词
     */
    private fun showScrollingDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(newLyric)
            .setMessage(lyricResult.msg)
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
        if (newMusic == null) {
            initMediaPlayer(musicPath + currentMusic.url)
        } else {
            initMediaPlayer(musicPath + newMusic)
        }
        val dialog = AlertDialog.Builder(context)
            .setTitle(newMusic)
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
        mediaPlayer.setOnPreparedListener {
            // 播放状态切换
            state.setOnClickListener(this)
            reset.setOnClickListener(this)
            duration = mediaPlayer.duration
            // 进度条设置最大值
            progressSeekBar.max = duration
            // 进度条变化监听
            progressSeekBar.setOnSeekBarChangeListener(this)
        }
    }

    /**
     * 初始化MediaPlayer
     */
    private fun initMediaPlayer(url: String) {
        try {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
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
     * 发送删除上传音乐相关文件缓存的请求
     */
    private fun deleteUploadMusicFileCache() {
        if (!isAddMusicSuccess) {
            if (!newMusicPoster.isNullOrEmpty() || !newMusicThumbnail.isNullOrEmpty()
                || !newLyric.isNullOrEmpty() || !newMusic.isNullOrEmpty()
            ) {
                if (token.isNotEmpty()) {
                    val music = MusicBean(
                        null, musicType, null, null, null,
                        newMusicPoster, newMusicThumbnail, newLyric, newMusic, newMusic,
                        newMusic, musicSize, null, null,
                        null, currentUser
                    )
                    deleteUploadMusicFileCachePresenter.deleteUploadMusicFileCache(token, music)
                }
            }
        }
    }

    /**
     * 弹窗显示音乐相关文件上传进度
     */
    private fun showUploadFileDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setView(R.layout.popup_upload_progress)
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
        progressInfo = dialog.findViewById(R.id.progress_info)!!
        progressText = dialog.findViewById(R.id.progress_text)!!
        currentText = dialog.findViewById(R.id.current_text)!!
        totalText = dialog.findViewById(R.id.total_text)!!
        uploadCompletedText = dialog.findViewById(R.id.upload_completed)!!
        progressBar = dialog.findViewById(R.id.progress_bar)!!
        hintInfo = dialog.findViewById(R.id.hint_info)!!
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

    override fun onUploadPosterProgress(progress: Int, total: Long, current: Long, done: Boolean) {
        if (done) {
            progressInfo.visibility = View.GONE
            uploadCompletedText.text =
                "上传完成 (${UnitUtil.bytesToSize(total)})"
            uploadCompletedText.visibility = View.VISIBLE
            hintInfo.visibility = View.GONE
        } else {
            uploadCompletedText.visibility = View.GONE
            progressInfo.visibility = View.VISIBLE
            hintInfo.visibility = View.VISIBLE
            progressText.text = progress.toString()
            currentText.text = UnitUtil.bytesToSize(current)
            totalText.text = UnitUtil.bytesToSize(total)
            progressBar.progress = progress
        }
    }

    override fun onUploadPosterSuccess(result: UploadFileResultBean) {
        posterPictureError.visibility = View.GONE
        newMusicPoster = result.data?.title.toString()
        // 将上传成功的音乐海报图片显示出来
        Glide.with(this)
            .load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.musicImagePath + newMusicPoster
            )
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(posterPicture)
    }

    override fun onUploadPosterFailed() {
        posterPictureError.text = "海报图片上传失败！"
        posterPictureError.visibility = View.VISIBLE
    }

    override fun onUploadThumbnailProgress(
        progress: Int,
        total: Long,
        current: Long,
        done: Boolean
    ) {
        if (done) {
            progressInfo.visibility = View.GONE
            uploadCompletedText.text =
                "上传完成 (${UnitUtil.bytesToSize(total)})"
            uploadCompletedText.visibility = View.VISIBLE
            hintInfo.visibility = View.GONE
        } else {
            uploadCompletedText.visibility = View.GONE
            progressInfo.visibility = View.VISIBLE
            hintInfo.visibility = View.VISIBLE
            progressText.text = progress.toString()
            currentText.text = UnitUtil.bytesToSize(current)
            totalText.text = UnitUtil.bytesToSize(total)
            progressBar.progress = progress
        }
    }

    override fun onUploadThumbnailSuccess(result: UploadFileResultBean) {
        thumbnailPictureError.visibility = View.GONE
        newMusicThumbnail = result.data?.title.toString()
        // 将上传成功的音乐缩略图图片显示出来
        Glide.with(this)
            .load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.musicImagePath + newMusicThumbnail
            )
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(thumbnailPicture)
    }

    override fun onUploadThumbnailFailed() {
        thumbnailPictureError.text = "缩略图上传失败！"
        thumbnailPictureError.visibility = View.VISIBLE
    }

    override fun onUploadLyricProgress(progress: Int, total: Long, current: Long, done: Boolean) {
        if (done) {
            progressInfo.visibility = View.GONE
            uploadCompletedText.text =
                "上传完成 (${UnitUtil.bytesToSize(total)})"
            uploadCompletedText.visibility = View.VISIBLE
            hintInfo.visibility = View.GONE
        } else {
            uploadCompletedText.visibility = View.GONE
            progressInfo.visibility = View.VISIBLE
            hintInfo.visibility = View.VISIBLE
            progressText.text = progress.toString()
            currentText.text = UnitUtil.bytesToSize(current)
            totalText.text = UnitUtil.bytesToSize(total)
            progressBar.progress = progress
        }
    }

    override fun onUploadLyricSuccess(result: UploadFileResultBean) {
        lyricError.visibility = View.GONE
        newLyric = result.data?.title.toString()
        lyric.visibility = View.GONE
        lyricName.visibility = View.VISIBLE
        lyricName.text = newLyric
        progressBar.progress = 100
        getLyricTextPresenter.getLyricText(newLyric!!)
    }

    override fun onUploadLyricFailed() {
        lyricError.text = "歌词文件上传失败！"
        lyricError.visibility = View.VISIBLE
    }

    override fun onGetLyricTextSuccess(result: ResultBean) {
        lyricResult = result
    }

    override fun onGetLyricTextFailed() {
        myToast(getString(R.string.get_lyric_text_error))
    }

    override fun onUploadMusicProgress(progress: Int, total: Long, current: Long, done: Boolean) {
        if (done) {
            progressInfo.visibility = View.GONE
            uploadCompletedText.text =
                "上传完成 (${UnitUtil.bytesToSize(total)})"
            uploadCompletedText.visibility = View.VISIBLE
            hintInfo.visibility = View.GONE
        } else {
            uploadCompletedText.visibility = View.GONE
            progressInfo.visibility = View.VISIBLE
            hintInfo.visibility = View.VISIBLE
            progressText.text = progress.toString()
            currentText.text = UnitUtil.bytesToSize(current)
            totalText.text = UnitUtil.bytesToSize(total)
            progressBar.progress = progress
        }
    }

    override fun onUploadMusicSuccess(result: UploadFileResultBean) {
        musicError.visibility = View.GONE
        newMusic = result.data?.title.toString()
        music.visibility = View.GONE
        musicName.visibility = View.VISIBLE
        musicName.text = newMusic
    }

    override fun onUploadMusicFailed() {
        musicError.text = "音乐文件上传失败！"
        musicError.visibility = View.VISIBLE
    }

    override fun onArtistNameError() {
        myToast(getString(R.string.artist_name_error))
        artistName.error = getString(R.string.artist_name_error)
    }

    override fun onDescriptionError() {
        myToast(getString(R.string.description_error))
        description.error = getString(R.string.description_error)
    }

    override fun onMusicPosterError() {
        myToast(getString(R.string.music_poster_error))
        posterPictureError.text = getString(R.string.music_poster_error)
        posterPictureError.visibility = View.VISIBLE
    }

    override fun onMusicThumbnailError() {
        myToast(getString(R.string.music_thumbnail_error))
        thumbnailPictureError.text = getString(R.string.music_thumbnail_error)
        thumbnailPictureError.visibility = View.VISIBLE
    }

    override fun onMusicFileError() {
        myToast(getString(R.string.music_file_error))
        musicError.text = getString(R.string.music_file_error)
        musicError.visibility = View.VISIBLE
    }

    override fun onTitleError() {
        myToast(getString(R.string.music_title_error))
        title.error = getString(R.string.music_title_error)
    }

    override fun onCheckTitleSuccess() {
        if (token.isNotEmpty()) {
            val musicTitle = title.text.trim().toString()
            val artistName = artistName.text.trim().toString()
            val description = description.text.trim().toString()
            val music = MusicBean(
                null, musicType, musicTitle, artistName, description,
                newMusicPoster, newMusicThumbnail, newLyric, newMusic, newMusic,
                newMusic, musicSize, musicSize, musicSize,
                0, currentUser
            )
            addMusicPresenter.addMusic(token, music)
        }
    }

    override fun onCheckTitleFailed(result: ResultBean) {
        myToast(getString(R.string.music_title_exist_error))
        title.error = getString(R.string.music_title_exist_error)
    }

    override fun onAddMusicSuccess() {
        isAddMusicSuccess = true
        myToast(getString(R.string.add_music_success))
        startActivityAndFinish<SuccessActivity>()
    }

    override fun onAddMusicFailed() {
        myToast(getString(R.string.add_music_failed))
    }

    override fun onDeleteUploadMusicFileCacheSuccess(result: ResultBean) {
        myToast(getString(R.string.delete_upload_music_file_cache_success))
    }

    override fun onDeleteUploadMusicFileCacheFailed(result: ResultBean) {
        myToast(getString(R.string.delete_upload_music_file_cache_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        deleteUploadMusicFileCache()
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        // 清空handler发送的所有消息
        handler.removeCallbacksAndMessages(null)
    }

}