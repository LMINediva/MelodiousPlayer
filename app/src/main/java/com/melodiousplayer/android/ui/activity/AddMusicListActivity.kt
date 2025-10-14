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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.PagingAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.AddMusicListContract
import com.melodiousplayer.android.contract.DeleteUploadMusicListFileCacheContract
import com.melodiousplayer.android.contract.GetMVListContract
import com.melodiousplayer.android.contract.UploadThumbnailContract
import com.melodiousplayer.android.model.MVListResultBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.UploadFileResultBean
import com.melodiousplayer.android.model.UserBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.presenter.impl.AddMusicListPresenterImpl
import com.melodiousplayer.android.presenter.impl.DeleteUploadMusicListFileCachePresenterImpl
import com.melodiousplayer.android.presenter.impl.GetMVListPresenterImpl
import com.melodiousplayer.android.presenter.impl.UploadMusicListThumbnailPresenterImpl
import com.melodiousplayer.android.util.DateUtil
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import com.melodiousplayer.android.util.UnitUtil
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.io.FileNotFoundException
import kotlin.math.ceil

/**
 * 添加音乐清单界面
 */
class AddMusicListActivity : BaseActivity(), ToolBarManager, GetMVListContract.View,
    View.OnClickListener, UploadThumbnailContract.View, AddMusicListContract.View,
    DeleteUploadMusicListFileCacheContract.View {

    private lateinit var title: EditText
    private lateinit var description: EditText
    private lateinit var category: EditText
    private lateinit var thumbnailPicture: ImageView
    private lateinit var thumbnailPictureError: TextView
    private lateinit var mvListError: TextView
    private lateinit var addMusicList: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PagingAdapter
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button
    private lateinit var pageInfo: TextView
    private lateinit var totalInfo: TextView
    private lateinit var spinnerPageSize: Spinner
    private lateinit var progressInfo: LinearLayout
    private lateinit var uploadCompletedText: TextView
    private lateinit var progressText: TextView
    private lateinit var currentText: TextView
    private lateinit var totalText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var hintInfo: TextView
    private lateinit var currentUser: UserBean
    private lateinit var token: String
    private val PERMISSION_REQUEST = 1
    private val ALBUM_THUMBNAIL_REQUEST = 1
    private val CROP_THUMBNAIL_REQUEST = 2
    private val getMVListPresenter = GetMVListPresenterImpl(this)
    private val uploadMusicListThumbnailPresenter = UploadMusicListThumbnailPresenterImpl(this)
    private val deleteUploadMusicListFileCachePresenter =
        DeleteUploadMusicListFileCachePresenterImpl(this)
    private val addMusicListPresenter = AddMusicListPresenterImpl(this)
    private var currentPage = 1
    private var totalPages = 1
    private var pageSize = 5
    private var total = 0L
    private var isUpdate = false
    private var dataList = mutableListOf<VideosBean>()
    private var selectedItems = mutableSetOf<VideosBean>()
    private var newMusicListThumbnail: String? = null
    private var isAddMusicListSuccess: Boolean = false

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_music_list
    }

    override fun initData() {
        initAddMusicListToolBar()
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
        description = findViewById(R.id.description)
        category = findViewById(R.id.category)
        thumbnailPicture = findViewById(R.id.thumbnailPicture)
        thumbnailPictureError = findViewById(R.id.thumbnailPictureError)
        mvListError = findViewById(R.id.mvListError)
        addMusicList = findViewById(R.id.addMusicList)
        recyclerView = findViewById(R.id.recyclerView)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        pageInfo = findViewById(R.id.pageInfo)
        totalInfo = findViewById(R.id.totalInfo)
        spinnerPageSize = findViewById(R.id.spinnerPageSize)
        val userSerialized = intent.getSerializableExtra("user")
        if (userSerialized != null) {
            currentUser = userSerialized as UserBean
        }
        // 从SharedPreferences文件中读取token的值
        token = getSharedPreferences("data", Context.MODE_PRIVATE)
            .getString("token", "").toString()
        // 设置RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = PagingAdapter(dataList, selectedItems) { item, isChecked ->
            if (!isUpdate) {
                if (isChecked) {
                    selectedItems.add(item)
                } else {
                    selectedItems.remove(item)
                }
            }
        }
        recyclerView.adapter = adapter
        // 分页获取MV数据
        getMVList(currentPage, pageSize)
        updatePageInfo()
        // 设置分页大小选择器
        ArrayAdapter.createFromResource(
            this, R.array.page_sizes, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerPageSize.adapter = adapter
        }
        requestPermissions()
    }

    override fun initListener() {
        // 页面大小下拉菜单点击事件
        spinnerPageSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                pageSize = resources.getStringArray(R.array.page_sizes)[position].toInt()
                currentPage = 1
                isUpdate = true
                getMVList(currentPage, pageSize)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        // RecyclerView监听滚动事件
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        // 滚动停止
                        isUpdate = false
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        // 正在拖动滚动
                        isUpdate = true
                    }

                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        // 滚动尚未停止，正在自动滑动到某个位置
                        isUpdate = true
                    }
                }
            }
        })
        // 按钮点击事件
        btnPrevious.setOnClickListener(this)
        btnNext.setOnClickListener(this)
        thumbnailPicture.setOnClickListener(this)
        addMusicList.setOnClickListener(this)
    }

    /**
     * Toolbar上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                deleteUploadMusicListFileCache()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.thumbnailPicture -> {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                // 通过setType方法限制类型为图像，否则有些android版本会同时显示视频
                intent.type = "image/*"
                startActivityForResult(intent, ALBUM_THUMBNAIL_REQUEST)
            }

            R.id.btnPrevious -> {
                if (currentPage > 1) {
                    currentPage--
                    isUpdate = true
                    getMVList(currentPage, pageSize)
                }
            }

            R.id.btnNext -> {
                if (currentPage < totalPages) {
                    currentPage++
                    isUpdate = true
                    getMVList(currentPage, pageSize)
                }
            }

            R.id.addMusicList -> {
                if (token.isNotEmpty()) {
                    val title = title.text.trim().toString()
                    val description = description.text.trim().toString()
                    val category = category.text.trim().toString()
                    val videoCount = selectedItems.size
                    val createTime = DateUtil.getCurrentTime()
                    val play = PlayListsBean(
                        null, title, newMusicListThumbnail, videoCount,
                        selectedItems, description, category, 0,
                        0, 0, null, createTime,
                        0, 0, 0, 0, currentUser
                    )
                    addMusicListPresenter.addMusicList(token, play)
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
                    // 将裁剪好的MV缩略图图片上传到服务器
                    if (resultUri != null) {
                        if (token.isNotEmpty()) {
                            showUploadFileDialog(this)
                            resultUri.path?.let { File(it) }
                                ?.let {
                                    uploadMusicListThumbnailPresenter.uploadThumbnail(
                                        token,
                                        it
                                    )
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
     * 分页获取MV列表数据
     */
    private fun getMVList(currentPage: Int, pageSize: Int) {
        val pageBean = PageBean("", currentPage, pageSize, null)
        getMVListPresenter.getMVList(token, pageBean)
    }

    /**
     * 更新MV列表数据
     */
    private fun updateMVList() {
        totalPages = ceil(total.toDouble() / pageSize).toInt()
        adapter.updateItems(dataList)
        updatePageInfo()
        updateButtonStates()
        // RecyclerView的post方法在数据渲染完成后执行操作
        recyclerView.post {
            isUpdate = false
        }
        for (mv in selectedItems) {
            println("id = ${mv.id}")
        }
    }

    /**
     * 更新页面信息
     */
    private fun updatePageInfo() {
        pageInfo.text = "${currentPage}/${totalPages}"
        totalInfo.text = "共${total}条"
    }

    /**
     * 更新上一页和下一页按钮的状态
     */
    private fun updateButtonStates() {
        btnPrevious.isEnabled = currentPage > 1
        btnNext.isEnabled = currentPage < totalPages
    }

    /**
     * 发送删除上传悦单相关文件缓存的请求
     */
    private fun deleteUploadMusicListFileCache() {
        if (!isAddMusicListSuccess) {
            if (!newMusicListThumbnail.isNullOrEmpty()) {
                if (token.isNotEmpty()) {
                    val play = PlayListsBean(
                        null, null, newMusicListThumbnail, null,
                        null, null, null, null,
                        null, null, null, null,
                        null, null, null, null, currentUser
                    )
                    deleteUploadMusicListFileCachePresenter.deleteUploadMusicListFileCache(
                        token,
                        play
                    )
                }
            }
        }
    }

    override fun onGetMVListSuccess(result: MVListResultBean) {
        dataList = result.mvList!!
        total = result.total!!
        updateMVList()
    }

    override fun onGetMVListFailed() {
        myToast(getString(R.string.get_mv_list_failed))
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
        newMusicListThumbnail = result.data?.title.toString()
        // 将上传成功的MV缩略图图片显示出来
        Glide.with(this)
            .load(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.mvImagePath + newMusicListThumbnail
            )
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(thumbnailPicture)
    }

    override fun onUploadThumbnailFailed() {
        thumbnailPictureError.text = "缩略图上传失败！"
        thumbnailPictureError.visibility = View.VISIBLE
    }

    override fun onMusicListTitleError() {
        myToast(getString(R.string.music_list_title_error))
        title.error = getString(R.string.music_list_title_error)
    }

    override fun onMusicListTitleExistError() {
        myToast(getString(R.string.music_list_title_exist_error))
        title.error = getString(R.string.music_list_title_exist_error)
    }

    override fun onDescriptionError() {
        myToast(getString(R.string.description_error))
        description.error = getString(R.string.description_error)
    }

    override fun onCategoryError() {
        myToast(getString(R.string.music_list_category_error))
        category.error = getString(R.string.music_list_category_error)
    }

    override fun onMusicListThumbnailError() {
        myToast(getString(R.string.music_list_thumbnail_error))
        thumbnailPictureError.text = getString(R.string.music_list_thumbnail_error)
        thumbnailPictureError.visibility = View.VISIBLE
    }

    override fun onMusicListMVQuantityError() {
        myToast(getString(R.string.music_list_mv_list_size_error))
        mvListError.text = getString(R.string.music_list_mv_list_size_error)
        mvListError.visibility = View.VISIBLE
    }

    override fun onAddMusicListSuccess() {
        isAddMusicListSuccess = true
        myToast(getString(R.string.add_music_list_success))
        startActivityAndFinish<SuccessActivity>()
    }

    override fun onAddMusicListFailed() {
        myToast(getString(R.string.add_music_list_failed))
    }

    override fun onDeleteUploadMusicListFileCacheSuccess(result: ResultBean) {
        myToast(getString(R.string.delete_upload_music_list_file_cache_success))
    }

    override fun onDeleteUploadMusicListFileCacheFailed(result: ResultBean) {
        myToast(getString(R.string.delete_upload_music_list_file_cache_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onBackPressed() {
        deleteUploadMusicListFileCache()
        finish()
        super.onBackPressed()
    }

}