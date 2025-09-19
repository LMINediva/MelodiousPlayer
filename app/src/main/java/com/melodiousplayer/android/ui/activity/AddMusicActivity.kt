package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
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
import com.melodiousplayer.android.util.ToolBarManager
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

/**
 * 添加音乐界面
 */
class AddMusicActivity : BaseActivity(), ToolBarManager, View.OnClickListener {

    private lateinit var title: EditText
    private lateinit var artistName: EditText
    private lateinit var description: EditText
    private lateinit var posterPicture: ImageView
    private lateinit var thumbnailPicture: ImageView
    private lateinit var lyric: ImageView
    private lateinit var lyricName: TextView
    private lateinit var music: ImageView
    private lateinit var musicName: TextView
    private lateinit var addMusic: Button
    private lateinit var progress: TextView
    private lateinit var progressSeekBar: SeekBar
    private lateinit var start: ImageView
    private lateinit var pause: ImageView
    private lateinit var reset: ImageView
    private lateinit var token: String
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
        thumbnailPicture = findViewById(R.id.thumbnailPicture)
        lyric = findViewById(R.id.lyric)
        lyricName = findViewById(R.id.lyricName)
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

            R.id.start -> {
                if (!mediaPlayer.isPlaying) {
                    mediaPlayer.start()
                }
            }

            R.id.pause -> {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            }

            R.id.reset -> {
                mediaPlayer.reset()
                initMediaPlayer(musicUri)
            }

            R.id.addMusic -> {

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
                        // 在截图界面显示选择好的照片
                        val intent = CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .getIntent(this)
                        startActivityForResult(intent, CROP_POSTER_REQUEST)
                    }
                }
            }

            CROP_POSTER_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = CropImage.getActivityResult(data).uri
                    // 将裁剪好的照片显示出来
                    if (resultUri != null) {
                        println("poster uri = " + resultUri)
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
                        // 在截图界面显示选择好的照片
                        val intent = CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .getIntent(this)
                        startActivityForResult(intent, CROP_THUMBNAIL_REQUEST)
                    }
                }
            }

            CROP_THUMBNAIL_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = CropImage.getActivityResult(data).uri
                    // 将裁剪好的照片显示出来
                    if (resultUri != null) {
                        println("thumbnail uri = " + resultUri)
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
        start = dialog.findViewById(R.id.start)!!
        pause = dialog.findViewById(R.id.pause)!!
        reset = dialog.findViewById(R.id.reset)!!
        // 播放状态切换
        start.setOnClickListener(this)
        pause.setOnClickListener(this)
        reset.setOnClickListener(this)
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

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        mediaPlayer.release()
    }

}