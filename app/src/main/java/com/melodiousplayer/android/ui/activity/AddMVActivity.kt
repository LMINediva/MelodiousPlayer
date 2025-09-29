package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.DeleteUploadMVFileCacheContract
import com.melodiousplayer.android.contract.MVAreasContract
import com.melodiousplayer.android.contract.UploadMVContract
import com.melodiousplayer.android.contract.UploadPosterContract
import com.melodiousplayer.android.contract.UploadThumbnailContract
import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.presenter.impl.DeleteUploadMVFileCachePresenterImpl
import com.melodiousplayer.android.presenter.impl.MVAreasPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadMVPosterPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadMVPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadMVThumbnailPresenterImpl
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
 * 添加MV界面
 */
class AddMVActivity : BaseActivity(), ToolBarManager, AdapterView.OnItemSelectedListener,
    MVAreasContract.View, View.OnClickListener, UploadPosterContract.View,
    UploadThumbnailContract.View, UploadMVContract.View, DeleteUploadMVFileCacheContract.View {

    private lateinit var title: EditText
    private lateinit var artistName: EditText
    private lateinit var description: EditText
    private lateinit var areaSpinner: Spinner
    private lateinit var posterPicture: ImageView
    private lateinit var posterPictureError: TextView
    private lateinit var thumbnailPicture: ImageView
    private lateinit var thumbnailPictureError: TextView
    private lateinit var mv: ImageView
    private lateinit var mvName: TextView
    private lateinit var mvError: TextView
    private lateinit var addMV: Button
    private lateinit var progressInfo: LinearLayout
    private lateinit var uploadCompletedText: TextView
    private lateinit var progressText: TextView
    private lateinit var currentText: TextView
    private lateinit var totalText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var hintInfo: TextView
    private lateinit var videoPlayer: JzvdStd
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var currentUser: UserBean
    private lateinit var token: String
    private val PERMISSION_REQUEST = 1
    private val ALBUM_POSTER_REQUEST = 1
    private val CROP_POSTER_REQUEST = 2
    private val ALBUM_THUMBNAIL_REQUEST = 3
    private val CROP_THUMBNAIL_REQUEST = 4
    private val MV_FILE_REQUEST = 5
    private val items = mutableListOf<String>()
    private val mvAreasPresenterImpl = MVAreasPresenterImpl(this)
    private val uploadMVPosterPresenter = UploadMVPosterPresenterImpl(this)
    private val uploadMVThumbnailPresenter = UploadMVThumbnailPresenterImpl(this)
    private val uploadMVPresenter = UploadMVPresenterImpl(this)
    private val deleteUploadMVFileCachePresenter = DeleteUploadMVFileCachePresenterImpl(this)
    private var newMVPoster: String? = null
    private var newMVThumbnail: String? = null
    private var newMV: String? = null
    private var mvType: String? = null
    private var mvSize: Float = 0F
    private var hdMVSize: Float = 0F
    private var uhdMVSize: Float = 0F
    private var isAddMusicSuccess: Boolean = false

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_mv
    }

    override fun initData() {
        initAddMVToolBar()
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
        areaSpinner = findViewById(R.id.areaSpinner)
        posterPicture = findViewById(R.id.posterPicture)
        posterPictureError = findViewById(R.id.posterPictureError)
        thumbnailPicture = findViewById(R.id.thumbnailPicture)
        thumbnailPictureError = findViewById(R.id.thumbnailPictureError)
        mv = findViewById(R.id.mv)
        mvName = findViewById(R.id.mvName)
        mvError = findViewById(R.id.mvError)
        addMV = findViewById(R.id.addMV)
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
        }
        // 从SharedPreferences文件中读取token的值
        token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "").toString()
        mvAreasPresenterImpl.getMVAreas(token)
        // 创建一个ArrayAdapter使用simple_spinner_item布局文件作为下拉列表的样式
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        // 设置下拉列表的样式
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // 将ArrayAdapter设置给Spinner
        areaSpinner.adapter = adapter
        requestPermissions()
    }

    override fun initListener() {
        // 设置Spinner选中项变更监听器
        areaSpinner.onItemSelectedListener = this
        posterPicture.setOnClickListener(this)
        thumbnailPicture.setOnClickListener(this)
        mv.setOnClickListener(this)
        mvName.setOnClickListener(this)
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

    /**
     * Spinner选中项变更事件
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        println("选中的项：${parent?.getItemAtPosition(position)}")
    }

    /**
     * Spinner没有选中项事件
     */
    override fun onNothingSelected(parent: AdapterView<*>?) {

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

            R.id.mv -> {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, MV_FILE_REQUEST)
            }

            R.id.mvName -> {
                showPlayMusicDialog(this)
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
                                ?.let { uploadMVPosterPresenter.uploadPoster(token, it) }
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
                                ?.let { uploadMVThumbnailPresenter.uploadThumbnail(token, it) }
                        }
                    }
                }
            }

            MV_FILE_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = data?.data
                    if (resultUri != null) {
                        if (checkMV(resultUri)) {
                            if (token.isNotEmpty()) {
                                val file = getFileFromUri(resultUri)
                                if (file != null) {
                                    showUploadFileDialog(this)
                                    uploadMVPresenter.uploadMV(token, file)
                                } else {
                                    mvError.text = "MV文件路径不正确或文件已损坏！"
                                    mvError.visibility = View.VISIBLE
                                }
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
     * 检查MV文件是否符合要求
     */
    private fun checkMV(uri: Uri): Boolean {
        // 获取音乐文件的MIME类型
        val type = contentResolver.getType(uri)
        if (type == null) {
            // 无法获取MIME类型，可能文件路径不正确或文件不存在
            mvError.text = "MV文件路径不正确或文件不存在！"
            mvError.visibility = View.VISIBLE
            return false
        }
        // 检查是否是视频文件
        val isSupportedFormat = type.startsWith("video/")
        if (!isSupportedFormat) {
            // 不是视频文件
            mvError.text = "MV文件只能是视频文件！"
            mvError.visibility = View.VISIBLE
            return false
        }
        // 检查图片文件大小是否超过1GB（1024 * 1024 * 1024字节）
        var fileSize: Long = 0
        try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                // 获取文件大小（字节）
                fileSize = inputStream.available().toLong()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            mvError.text = "MV文件路径不正确或文件不存在！"
            mvError.visibility = View.VISIBLE
            return false
        }
        val isLessThan1GB = fileSize <= 1024 * 1024 * 1024
        if (!isLessThan1GB) {
            mvError.text = "MV文件大小不能超过1GB！"
            mvError.visibility = View.VISIBLE
            return false
        }
        val bigDecimal = BigDecimal(fileSize / (1024 * 1024))
        val fileSizeMB = bigDecimal.setScale(1, RoundingMode.HALF_UP).toFloat()
        mvSize = fileSizeMB
        hdMVSize = fileSizeMB
        uhdMVSize = fileSizeMB
        mvType = type
        return true
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
     * 弹窗显示MV相关文件上传进度
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
     * 弹窗显示MV播放控件
     */
    private fun showPlayMusicDialog(context: Context) {
        val dialog = AlertDialog.Builder(context)
            .setTitle(newMV)
            .setView(R.layout.popup_mv_player)
            .setCancelable(false)
            .setPositiveButton("确定") { dialog, which ->
                Jzvd.releaseAllVideos()
            }
            .create()
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()
        videoPlayer = dialog.findViewById(R.id.mv_player)!!
        // 从服务器请求上传的视频进行播放
        videoPlayer.setUp(
            URLProviderUtils.protocol + URLProviderUtils.serverAddress
                    + URLProviderUtils.mvPath + newMV,
            newMV,
            JzvdStd.SCREEN_NORMAL
        )
        // 设置视频默认缩略图
        Glide.with(this)
            .load(R.mipmap.default_poster)
            .into(videoPlayer.posterImageView)
    }

    /**
     * 发送删除上传音乐相关文件缓存的请求
     */
    private fun deleteUploadMusicFileCache() {
        if (!isAddMusicSuccess) {
            if (!newMVPoster.isNullOrEmpty() || !newMVThumbnail.isNullOrEmpty()
                || !newMV.isNullOrEmpty()
            ) {
                if (token.isNotEmpty()) {
                    val mv = VideosBean(
                        null, newMV, null, null, newMVPoster,
                        newMVThumbnail, null, mvType, null, null,
                        null, null, newMV, newMV, newMV,
                        mvSize, hdMVSize, uhdMVSize, null, null, currentUser
                    )
                    deleteUploadMVFileCachePresenter.deleteUploadMVFileCache(token, mv)
                }
            }
        }
    }

    override fun onGetMVAreasSuccess(result: List<MVAreaBean>) {
        adapter.clear()
        for (mvAreaBean in result) {
            items.add(mvAreaBean.name)
        }
        adapter.notifyDataSetChanged()
    }

    override fun onGetMVAreasFailed() {
        myToast(getString(R.string.get_mv_areas_failed))
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
        newMVPoster = result.data?.title.toString()
        // 将上传成功的音乐海报图片显示出来
        Glide.with(this)
            .load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.musicImagePath + newMVPoster
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
        newMVThumbnail = result.data?.title.toString()
        // 将上传成功的音乐缩略图图片显示出来
        Glide.with(this)
            .load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.musicImagePath + newMVThumbnail
            )
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(thumbnailPicture)
    }

    override fun onUploadThumbnailFailed() {
        thumbnailPictureError.text = "海报缩略图上传失败！"
        thumbnailPictureError.visibility = View.VISIBLE
    }

    override fun onUploadMVProgress(progress: Int, total: Long, current: Long, done: Boolean) {
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

    override fun onUploadMVSuccess(result: UploadFileResultBean) {
        mvError.visibility = View.GONE
        newMV = result.data?.title.toString()
        mv.visibility = View.GONE
        mvName.visibility = View.VISIBLE
        mvName.text = newMV
    }

    override fun onUploadMVFailed() {
        mvError.text = "MV文件上传失败！"
        mvError.visibility = View.VISIBLE
    }

    override fun onDeleteUploadMVFileCacheSuccess(result: ResultBean) {
        myToast(getString(R.string.delete_upload_mv_file_cache_success))
    }

    override fun onDeleteUploadMVFileCacheFailed(result: ResultBean) {
        myToast(getString(R.string.delete_upload_mv_file_cache_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        deleteUploadMusicFileCache()
        finish()
        super.onBackPressed()
    }

}