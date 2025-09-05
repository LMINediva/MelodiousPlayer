package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.util.DateUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

/**
 * 个人信息界面
 */
class UserInfoActivity : BaseActivity(), ToolBarManager, View.OnClickListener {

    private lateinit var avatarImage: CircleImageView
    private lateinit var changePicture: ImageView
    private lateinit var username: EditText
    private lateinit var phonenumber: EditText
    private lateinit var email: EditText
    private lateinit var role: TextView
    private lateinit var createTime: TextView
    private lateinit var albums: TextView
    private lateinit var cancel: LinearLayout
    private lateinit var photograph: TextView
    private var popupWindow: PopupWindow? = null
    private val CAMERA_PERMISSION_REQUEST = 1
    private val PERMISSION_REQUEST = 1
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2
    private val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 3
    private val TAKE_PHOTO_REQUEST = 1
    private val CROP_PHOTO_REQUEST = 2
    private val ALBUM_REQUEST = 3
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File
    private var hasAllPermission: Boolean = true

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_user_info
    }

    override fun initData() {
        initUserInfoToolBar()
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
        createTime = findViewById(R.id.createTime)
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            val currentUser = userSerialized as UserBean
            username.text = Editable.Factory.getInstance().newEditable(currentUser.username)
            phonenumber.text = Editable.Factory.getInstance().newEditable(currentUser.phonenumber)
            email.text = Editable.Factory.getInstance().newEditable(currentUser.email)
            role.text = currentUser.roles
            createTime.text = currentUser.createTime?.let { DateUtil.formatDateToString(it) }
            Glide.with(this).load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.userAvatarPath + currentUser.avatar
            ).into(avatarImage)
        }
        requestPermissions()
    }

    override fun initListener() {
        avatarImage.setOnClickListener(this)
        changePicture.setOnClickListener(this)
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
                // 打开文件选择器
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                // 指定只显示图片
                intent.type = "image/*"
                startActivityForResult(intent, ALBUM_REQUEST)
                popupWindow?.dismiss()
            }

            R.id.cancel -> {
                popupWindow?.dismiss()
            }
        }
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
                    hasAllPermission = false
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
            photograph = view.findViewById(R.id.photograph)
            albums = view.findViewById(R.id.albums)
            cancel = view.findViewById(R.id.cancel)
            popupWindow = PopupWindow(view, width, height, true)
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
                    val bitmap = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(resultUri)
                    )
                    // 将拍摄并裁剪好的照片显示出来
                    avatarImage.setImageBitmap(bitmap)
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

    private fun getBitmapFromUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

}