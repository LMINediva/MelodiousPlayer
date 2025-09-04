package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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
    private val TAKE_PHOTO_REQUEST = 1
    private val CROP_PHOTO_REQUEST = 2
    private val ALBUM_REQUEST = 3
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File

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
    }

    override fun initListener() {
        avatarImage.setOnClickListener(this)
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
                if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.CAMERA),
                            CAMERA_PERMISSION_REQUEST
                        )
                    } else {
                        takePhoto()
                    }
                } else {
                    myToast("设备没有相机")
                }
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
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    takePhoto()
                } else {
                    myToast("相机权限被拒绝")
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
            albums = view.findViewById(R.id.albums)
            photograph = view.findViewById(R.id.photograph)
            cancel = view.findViewById(R.id.cancel)
            popupWindow = PopupWindow(view, width, height, true)
            photograph.setOnClickListener(this)
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
                    // 将拍摄的照片显示出来
                    val bitmap = BitmapFactory.decodeStream(
                        contentResolver.openInputStream(imageUri)
                    )
                    avatarImage.setImageBitmap(rotateIfRequired(bitmap))
                    // 销毁临时文件
                    // outputImage.delete()
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

    private fun rotateIfRequired(bitmap: Bitmap): Bitmap {
        val exif = ExifInterface(outputImage.path)
        val orientation = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width,
            bitmap.height, matrix, true
        )
        // 将不再需要的Bitmap对象回收
        bitmap.recycle()
        return rotatedBitmap
    }

    private fun getBitmapFromUri(uri: Uri) = contentResolver
        .openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }

}