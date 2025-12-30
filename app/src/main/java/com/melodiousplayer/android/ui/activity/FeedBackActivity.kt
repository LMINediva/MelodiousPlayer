package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.AddFeedBackContract
import com.melodiousplayer.android.contract.UploadPictureContract
import com.melodiousplayer.android.model.FeedBackBean
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.presenter.impl.AddFeedBackPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadFeedBackPicturePresenterImpl
import com.melodiousplayer.android.util.DateUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import com.melodiousplayer.android.util.UnitUtil
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileNotFoundException

/**
 * 用户反馈界面
 */
class FeedBackActivity : BaseActivity(), ToolBarManager, View.OnClickListener,
    UploadPictureContract.View, AddFeedBackContract.View {

    private lateinit var content: EditText
    private lateinit var feedbackPicture: ImageView
    private lateinit var feedbackPictureError: TextView
    private lateinit var addFeedback: Button
    private lateinit var progressInfo: LinearLayout
    private lateinit var uploadCompletedText: TextView
    private lateinit var progressText: TextView
    private lateinit var currentText: TextView
    private lateinit var totalText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var hintInfo: TextView
    private lateinit var token: String
    private lateinit var currentUser: UserBean
    private val PERMISSION_REQUEST = 1
    private val ALBUM_FEEDBACK_REQUEST = 1
    private val CROP_FEEDBACK_REQUEST = 2
    private val uploadFeedBackPicturePresenter = UploadFeedBackPicturePresenterImpl(this)
    private val addFeedBackPresenter = AddFeedBackPresenterImpl(this)
    private var newFeedBackPicture: String? = null
    private var isAddFeedBackSuccess: Boolean = false

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_feedback
    }

    override fun initData() {
        initFeedBackToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        content = findViewById(R.id.content)
        feedbackPicture = findViewById(R.id.feedbackPicture)
        feedbackPictureError = findViewById(R.id.feedbackPictureError)
        addFeedback = findViewById(R.id.addFeedback)
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
        }
        // 从SharedPreferences文件中读取token的值
        token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "").toString()
        requestPermissions()
    }

    override fun initListener() {
        feedbackPicture.setOnClickListener(this)
        addFeedback.setOnClickListener(this)
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
            R.id.feedbackPicture -> {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // 通过setType方法限制类型为图像，否则有些android版本会同时显示视频
                intent.type = "image/*"
                startActivityForResult(intent, ALBUM_FEEDBACK_REQUEST)
            }

            R.id.addFeedback -> {
                if (token.isNotEmpty()) {
                    val content = content.text.trim().toString()
                    val submissionTime = DateUtil.getCurrentTime()
                    val feedback = FeedBackBean(
                        null, content, newFeedBackPicture,
                        submissionTime, currentUser
                    )
                    addFeedBackPresenter.addFeedBack(token, feedback)
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
            ALBUM_FEEDBACK_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        if (checkPicture(uri, feedbackPictureError)) {
                            // 在截图界面显示选择好的照片
                            val intent = CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setInitialCropWindowPaddingRatio(0F)
                                .getIntent(this)
                            startActivityForResult(intent, CROP_FEEDBACK_REQUEST)
                        }
                    }
                }
            }

            CROP_FEEDBACK_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = CropImage.getActivityResult(data).uri
                    // 将裁剪好的音乐海报图片上传到服务器
                    if (resultUri != null) {
                        if (token.isNotEmpty()) {
                            showUploadFileDialog(this)
                            resultUri.path?.let { File(it) }
                                ?.let { uploadFeedBackPicturePresenter.uploadPicture(token, it) }
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
     * 弹窗显示用户反馈相关文件上传进度
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

    override fun onUploadPictureProgress(progress: Int, total: Long, current: Long, done: Boolean) {
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

    override fun onUploadPictureSuccess(result: UploadFileResultBean) {
        feedbackPictureError.visibility = View.GONE
        newFeedBackPicture = result.data?.title.toString()
        // 将上传成功的用户反馈图片显示出来
        Glide.with(this)
            .load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.musicImagePath + newFeedBackPicture
            )
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(feedbackPicture)
    }

    override fun onUploadPictureFailed() {
        feedbackPictureError.text = "用户反馈图片上传失败！"
        feedbackPictureError.visibility = View.VISIBLE
    }

    override fun onFeedBackContentError() {
        myToast(getString(R.string.feedback_error))
        content.error = getString(R.string.feedback_error)
    }

    override fun onFeedBackContentLengthError() {
        myToast(getString(R.string.feedback_length_error))
        content.error = getString(R.string.feedback_length_error)
    }

    override fun onAddFeedBackSuccess() {
        isAddFeedBackSuccess = true
        myToast(getString(R.string.add_feedback_success))
        startActivityAndFinish<SuccessActivity>()
    }

    override fun onAddFeedBackFailed() {
        myToast(getString(R.string.add_feedback_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }

}