package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.UpdateAvatarContract
import com.melodiousplayer.android.contract.UpdateUserInfoContract
import com.melodiousplayer.android.contract.UploadAvatarContract
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.presenter.impl.UpdateAvatarPresenterImpl
import com.melodiousplayer.android.presenter.impl.UpdateUserInfoPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadAvatarPresenterImpl
import com.melodiousplayer.android.util.DateUtil
import com.melodiousplayer.android.util.ThemeUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import com.melodiousplayer.android.util.UnitUtil
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

/**
 * 个人信息界面
 */
class UserInfoActivity : BaseActivity(), ToolBarManager, View.OnClickListener,
    UploadAvatarContract.View, UpdateAvatarContract.View, UpdateUserInfoContract.View {

    private lateinit var avatarImage: CircleImageView
    private lateinit var changePicture: ImageView
    private lateinit var username: EditText
    private lateinit var phonenumber: EditText
    private lateinit var email: EditText
    private lateinit var role: TextView
    private lateinit var updateTime: TextView
    private lateinit var createTime: TextView
    private lateinit var popupBackground: RelativeLayout
    private lateinit var line: TextView
    private lateinit var albums: TextView
    private lateinit var cancel: LinearLayout
    private lateinit var photograph: TextView
    private lateinit var updateButton: Button
    private lateinit var progressInfo: LinearLayout
    private lateinit var uploadCompletedText: TextView
    private lateinit var progressText: TextView
    private lateinit var currentText: TextView
    private lateinit var totalText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var hintInfo: TextView
    private lateinit var editItem: MenuItem
    private lateinit var cancelItem: MenuItem
    private var isDarkTheme: Boolean = false
    private var popupWindow: PopupWindow? = null
    private val PERMISSION_REQUEST = 1
    private val TAKE_PHOTO_REQUEST = 1
    private val CROP_PHOTO_REQUEST = 2
    private val ALBUM_REQUEST = 3
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File
    private lateinit var currentUser: UserBean
    private lateinit var token: String
    private var newAvatar: String? = null
    private var newUsername: String? = null
    private val uploadAvatarPresenter = UploadAvatarPresenterImpl(this)
    private val updateAvatarPresenter = UpdateAvatarPresenterImpl(this)
    private val updateUserInfoPresenter = UpdateUserInfoPresenterImpl(this)

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_info
    }

    override fun initData() {
        initUserInfoToolBar(this)
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        avatarImage = findViewById(R.id.userAvatar)
        changePicture = findViewById(R.id.changePicture)
        username = findViewById(R.id.userName)
        phonenumber = findViewById(R.id.phonenumber)
        email = findViewById(R.id.email)
        role = findViewById(R.id.role)
        updateTime = findViewById(R.id.updateTime)
        createTime = findViewById(R.id.createTime)
        updateButton = findViewById(R.id.update)
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
            username.text = Editable.Factory.getInstance().newEditable(currentUser.username)
            phonenumber.text = Editable.Factory.getInstance().newEditable(currentUser.phonenumber)
            email.text = Editable.Factory.getInstance().newEditable(currentUser.email)
            role.text = currentUser.roles
            updateTime.text = currentUser.updateTime?.let { DateUtil.formatDateToString(it) }
            createTime.text = currentUser.createTime?.let { DateUtil.formatDateToString(it) }
            Glide.with(this)
                .load(
                    URLProviderUtils.protocol + URLProviderUtils.serverAddress
                            + URLProviderUtils.userAvatarPath + currentUser.avatar
                )
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(avatarImage)
        }
        // 从SharedPreferences文件中读取token的值
        token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "").toString()
        isDarkTheme = ThemeUtil.isDarkTheme(this)
        requestPermissions()
    }

    override fun initListener() {
        changePicture.setOnClickListener(this)
        updateButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.userAvatar -> {
                showPopupWindow()
            }

            R.id.changePicture -> {
                showPopupWindow()
            }

            R.id.photograph -> {
                takePhoto()
                popupWindow?.dismiss()
            }

            R.id.albums -> {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // 通过setType方法限制类型为图像，否则有些android版本会同时显示视频
                intent.type = "image/*"
                startActivityForResult(intent, ALBUM_REQUEST)
                popupWindow?.dismiss()
            }

            R.id.cancel -> {
                popupWindow?.dismiss()
            }

            R.id.update -> {
                if (token.isNotEmpty()) {
                    currentUser.username = username.text.trim().toString()
                    currentUser.phonenumber = phonenumber.text.trim().toString()
                    currentUser.email = email.text.trim().toString()
                    currentUser.updateTime = null
                    currentUser.createTime = null
                    // 更新用户信息
                    updateUserInfoPresenter.updateUserInfo(token, currentUser)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 设置action按钮
        menuInflater.inflate(R.menu.edit_menu, menu)
        editItem = menu!!.findItem(R.id.edit)
        cancelItem = menu.findItem(R.id.cancel)
        return true
    }

    /**
     * Toolbar上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                username.isClickable = true
                username.isFocusable = true
                username.isFocusableInTouchMode = true
                phonenumber.isClickable = true
                phonenumber.isFocusable = true
                phonenumber.isFocusableInTouchMode = true
                email.isClickable = true
                email.isFocusable = true
                email.isFocusableInTouchMode = true
                editItem.isVisible = false
                cancelItem.isVisible = true
                avatarImage.setOnClickListener(this)
                changePicture.visibility = View.VISIBLE
                updateButton.visibility = View.VISIBLE
            }

            R.id.cancel -> {
                username.isClickable = false
                username.isFocusable = false
                username.isFocusableInTouchMode
                phonenumber.isClickable = false
                phonenumber.isFocusable = false
                phonenumber.isFocusableInTouchMode = false
                email.isClickable = false
                email.isFocusable = false
                email.isFocusableInTouchMode = false
                editItem.isVisible = true
                cancelItem.isVisible = false
                avatarImage.setOnClickListener(null)
                changePicture.visibility = View.GONE
                updateButton.visibility = View.GONE
            }

            android.R.id.home -> {
                val intent = Intent()
                intent.putExtra("newAvatar", newAvatar)
                setResult(RESULT_OK, intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestPermissions() {
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
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
        } else {
            myToast("设备没有相机")
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

    /**
     * 从底部显示PopupWindow
     */
    private fun showPopupWindow() {
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (popupWindow == null) {
            val view = layoutInflater.inflate(R.layout.popup_select_photograph, null)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = 500
            popupBackground = view.findViewById(R.id.popup_background)
            line = view.findViewById(R.id.line)
            photograph = view.findViewById(R.id.photograph)
            albums = view.findViewById(R.id.albums)
            cancel = view.findViewById(R.id.cancel)
            popupWindow = PopupWindow(view, width, height, true)
            if (isDarkTheme) {
                popupBackground.setBackgroundResource(R.color.lightGrayNight)
                line.setBackgroundResource(R.color.darkGray)
                photograph.setBackgroundResource(R.drawable.btn_up_select_night)
                albums.setBackgroundResource(R.drawable.btn_down_select_night)
                cancel.setBackgroundResource(R.drawable.btn_select_night)
            }
            photograph.setOnClickListener(this)
            albums.setOnClickListener(this)
            cancel.setOnClickListener(this)
        }
        popupWindow?.animationStyle = R.style.bottom_popup
        popupWindow?.isOutsideTouchable = true
        popupWindow?.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        // 显示PopupWindow，参数为锚点View和重力、偏移量，这里设置为底部弹出
        popupWindow?.showAtLocation(window.decorView, Gravity.BOTTOM, 0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            TAKE_PHOTO_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    // 在截图界面显示拍摄好的照片
                    val intent = CropImage.activity(imageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .getIntent(this)
                    startActivityForResult(intent, CROP_PHOTO_REQUEST)
                }
            }

            CROP_PHOTO_REQUEST -> {
                if (resultCode == RESULT_OK) {
                    val resultUri = CropImage.getActivityResult(data).uri
                    // 将拍摄并裁剪好的照片上传到服务器
                    if (resultUri != null) {
                        if (token.isNotEmpty()) {
                            showUploadFileDialog(this)
                            resultUri.path?.let { File(it) }
                                ?.let { uploadAvatarPresenter.uploadAvatar(token, it) }
                        }
                    }
                }
            }

            ALBUM_REQUEST -> {
                if (resultCode == RESULT_OK && data != null) {
                    data.data?.let { uri ->
                        // 在截图界面显示选择好的照片
                        val intent = CropImage.activity(uri)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .getIntent(this)
                        startActivityForResult(intent, CROP_PHOTO_REQUEST)
                    }
                }
            }
        }
    }

    /**
     * 拍照
     */
    private fun takePhoto() {
        // 创建File对象，用于存储拍照后的图片
        outputImage = File(externalCacheDir, "output_image.jpg")
        // 销毁临时文件
        if (outputImage.exists()) {
            outputImage.delete()
        }
        outputImage.createNewFile()
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                this, "com.melodiousplayer.android.fileprovider", outputImage
            )
        } else {
            Uri.fromFile(outputImage)
        }
        // 启动相机程序
        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, TAKE_PHOTO_REQUEST)
    }

    /**
     * 弹窗显示头像图片上传进度
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

    override fun onUploadAvatarProgress(progress: Int, total: Long, current: Long, done: Boolean) {
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

    override fun onUploadAvatarSuccess(result: UploadFileResultBean) {
        val user = UserBean(
            currentUser.id, null, null,
            result.data?.title, null, null, null,
            null, null, null, null, null,
            null, null
        )
        newAvatar = result.data?.title.toString()
        // 发送更新头像请求
        updateAvatarPresenter.updateAvatar(token, user)
    }

    override fun onUploadAvatarFailed() {
        myToast(getString(R.string.upload_avatar_failed))
    }

    override fun onUpdateAvatarSuccess() {
        // 将上传并更新成功的头像显示出来
        Glide.with(this)
            .load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.userAvatarPath + newAvatar
            )
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(avatarImage)
        myToast(getString(R.string.update_avatar_success))
    }

    override fun onUpdateAvatarFailed() {
        myToast(getString(R.string.update_avatar_failed))
    }

    override fun onUserNameError() {
        myToast(getString(R.string.user_name_error))
        username.error = getString(R.string.user_name_error)
    }

    override fun onPhoneNumberError() {
        myToast(getString(R.string.phone_number_error))
        phonenumber.error = getString(R.string.phone_number_error)
    }

    override fun onEmailError() {
        myToast(getString(R.string.email_error))
        email.error = getString(R.string.email_error)
    }

    override fun onUpdateSuccess() {
        myToast(getString(R.string.update_user_info_success))
        newUsername = username.text.trim().toString()
    }

    override fun onUpdateFailed() {
        myToast(getString(R.string.update_user_info_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("newAvatar", newAvatar)
        intent.putExtra("newUsername", newUsername)
        setResult(RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }

}