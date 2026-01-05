package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.VideoPagerAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.contract.DeleteMVContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.VideoPlayBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.presenter.impl.DeleteMVPresenterImpl
import com.melodiousplayer.android.util.FileUtil
import com.melodiousplayer.android.util.FileUtil.createTemporalFileFrom
import com.melodiousplayer.android.util.ToolBarManager
import com.melodiousplayer.android.util.URLProviderUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class JiaoZiVideoPlayerActivity : BaseActivity(), ToolBarManager, View.OnClickListener,
    DeleteMVContract.View {

    private lateinit var videoPlayer: JzvdStd
    private lateinit var viewPager: ViewPager
    private lateinit var rg: RadioGroup
    private lateinit var cancel: ImageView
    private lateinit var edit: ImageView
    private lateinit var delete: ImageView
    private lateinit var currentMV: VideosBean
    private var data: Uri? = null
    private var hasPermissions: Boolean = true
    private var videoPlayBean: VideoPlayBean? = null
    private var popupWindow: PopupWindow? = null
    private var token: String? = null
    private var isMyMV: Boolean = false
    private val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val EDIT_MV_REQUEST = 1
    private val client by lazy { OkHttpClient() }
    private val deleteMVPresenter = DeleteMVPresenterImpl(this)

    override val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    override val toolbarTitle by lazy { findViewById<TextView>(R.id.toolbar_title) }

    override fun getLayoutId(): Int {
        return R.layout.activity_video_player_jiaozi
    }

    override fun initData() {
        initVideoPlayerToolBar()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            // 启用Toolbar的返回按钮
            it.setDisplayHomeAsUpEnabled(true)
            // 显示返回按钮
            it.setDisplayShowHomeEnabled(true)
            // 隐藏默认标题
            it.setDisplayShowTitleEnabled(false)
        }
        videoPlayer = findViewById(R.id.jz_video)
        viewPager = findViewById(R.id.viewPager)
        rg = findViewById(R.id.rg)
        isMyMV = intent.getBooleanExtra("isMyMV", false)
        if (isMyMV) {
            token = intent.getStringExtra("token")
            val mvSerialized = intent.getSerializableExtra("mv")
            if (mvSerialized != null) {
                currentMV = mvSerialized as VideosBean
            }
        }
        data = intent.data
        if (data == null) {
            // 获取传递的数据
            videoPlayBean = intent.getParcelableExtra("item")
            // 从应用内响应视频播放
            videoPlayer.setUp(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + URLProviderUtils.mvPath + videoPlayBean?.url,
                videoPlayBean?.title,
                JzvdStd.SCREEN_NORMAL
            )
            // 设置视频缩略图
            Glide.with(this)
                .load(
                    URLProviderUtils.protocol + URLProviderUtils.serverAddress
                            + URLProviderUtils.mvImagePath + videoPlayBean?.thumbnailPic
                )
                .into(videoPlayer.posterImageView)
        } else {
            if (data.toString().startsWith("http")) {
                // 应用外的网络视频请求
                // 应用外响应
                val fileName: String = FileUtil.getFileNameFromUrl(data.toString())
                videoPlayer.setUp(data?.toString(), fileName, JzvdStd.SCREEN_NORMAL)
                // 使用视频第一帧作为缩略图
                GlobalScope.launch(Dispatchers.IO) {
                    val thumbnail: Bitmap? = downloadVideoAndGetFrame(data.toString(), fileName)
                    withContext(Dispatchers.Main) {
                        thumbnail?.let {
                            videoPlayer.posterImageView.setImageBitmap(thumbnail)
                        }
                    }
                }
            } else {
                // 应用外的本地视频请求
                // 动态申请权限
                hasPermissions = hasPermissions(this, permissions)
                if (!hasPermissions) {
                    ActivityCompat.requestPermissions(this, permissions, 1)
                } else {
                    // 应用外响应
                    val filePath = FileUtil.getFileFromUri(data, this)?.absolutePath
                    videoPlayer.setUp(filePath, filePath.toString(), JzvdStd.SCREEN_NORMAL)
                    // 设置视频缩略图
                    filePath?.let {
                        videoPlayer.posterImageView.setImageBitmap(
                            ThumbnailUtils.createVideoThumbnail(
                                filePath,
                                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
                            )
                        )
                    }
                }
            }
        }
    }

    override fun initListener() {
        // 适配ViewPager
        viewPager.adapter = VideoPagerAdapter(supportFragmentManager, videoPlayBean)
        // RadioGroup选中监听
        rg.setOnCheckedChangeListener { radioGroup, i ->
            when (i) {
                R.id.rb1 -> viewPager.setCurrentItem(0)
                R.id.rb2 -> viewPager.setCurrentItem(1)
                R.id.rb3 -> viewPager.setCurrentItem(2)
            }
        }

        // ViewPager选中状态监听
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            /**
             * 滑动状态改变的回调
             */
            override fun onPageScrollStateChanged(state: Int) {

            }

            /**
             * 滑动回调
             */
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            /**
             * 选中状态改变回调
             */
            override fun onPageSelected(position: Int) {
                when (position) {
                    0 -> rg.check(R.id.rb1)
                    1 -> rg.check(R.id.rb2)
                    2 -> rg.check(R.id.rb3)
                }
            }

        })
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.cancel -> popupWindow?.dismiss()
            R.id.edit -> editMyMV()
            R.id.delete -> {
                popupWindow?.dismiss()
                AlertDialog.Builder(this).apply {
                    setTitle("提示")
                    setMessage("确定要删除MV吗？")
                    setCancelable(false)
                    setPositiveButton("确定") { dialog, which ->
                        token?.let {
                            deleteMVPresenter.deleteMV(
                                it,
                                arrayOf(currentMV.id!!)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // 设置action按钮
        menuInflater.inflate(R.menu.more_operation, menu)
        if (!isMyMV) {
            menu?.findItem(R.id.more)?.setVisible(false)
        }
        return true
    }

    /**
     * Toolbar上的图标按钮点击事件
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

            R.id.more -> {
                showPopupWindow()
            }
        }
        return true
    }

    /**
     * 从底部显示PopupWindow
     */
    private fun showPopupWindow() {
        val layoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (popupWindow == null) {
            val view = layoutInflater.inflate(R.layout.popup_operation, null)
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = 350
            cancel = view.findViewById(R.id.cancel)
            edit = view.findViewById(R.id.edit)
            delete = view.findViewById(R.id.delete)
            popupWindow = PopupWindow(view, width, height, true)
            cancel.setOnClickListener(this)
            edit.setOnClickListener(this)
            delete.setOnClickListener(this)
        }
        popupWindow?.animationStyle = R.style.bottom_popup
        popupWindow?.isOutsideTouchable = true
        popupWindow?.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        // 显示PopupWindow，参数为锚点View和重力、偏移量，这里设置为底部弹出
        popupWindow?.showAtLocation(window.decorView, Gravity.BOTTOM, 0, 0)
    }

    /**
     * 修改我的MV
     */
    private fun editMyMV() {
        popupWindow?.dismiss()
        // 进入添加MV界面，传递MV信息
        val intent = Intent(this, AddMVActivity::class.java)
        intent.putExtra("isMyMV", true)
        intent.putExtra("mv", currentMV)
        startActivityForResult(intent, EDIT_MV_REQUEST)
    }

    /**
     * 从网络上下载视频文件，并获取视频文件的第一帧图片
     */
    private fun downloadVideoAndGetFrame(url: String, fileName: String): Bitmap? {
        var file: File? = null
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Failed to download video.")
            response.body?.byteStream()?.use { inputStream ->
                file = createTemporalFileFrom(this, inputStream, fileName)
            }
        }
        val filePath: String = file!!.path
        return getVideoFrameFromLocalFile(filePath)
    }

    /**
     * 获取视频文件的第一帧图片
     */
    private fun getVideoFrameFromLocalFile(filePath: String): Bitmap? {
        val retriever = MediaMetadataRetriever()
        try {
            retriever.setDataSource(filePath)
            return retriever.frameAtTime
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return null
    }

    /**
     * 检查多个权限是否授权
     */
    private fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                var allPermissionsGranted = true
                for (result in grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        allPermissionsGranted = false
                        break
                    }
                }
                if (allPermissionsGranted) {
                    // 应用外响应
                    val filePath = FileUtil.getFileFromUri(data, this)?.absolutePath
                    videoPlayer.setUp(filePath, filePath.toString(), JzvdStd.SCREEN_NORMAL)
                    // 设置视频缩略图
                    filePath?.let {
                        videoPlayer.posterImageView.setImageBitmap(
                            ThumbnailUtils.createVideoThumbnail(
                                filePath,
                                MediaStore.Video.Thumbnails.FULL_SCREEN_KIND
                            )
                        )
                    }
                } else {
                    myToast("你拒绝了权限")
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
    }

    override fun onDeleteMVSuccess(result: ResultBean) {
        myToast(getString(R.string.delete_mv_success))
        if (videoPlayer.state == Jzvd.STATE_PLAYING) {
            videoPlayer.onClickUiToggle()
            videoPlayer.startButton.performClick()
        }
        // 返回我的作品界面，传递用户信息，并退出JiaoZiVideoPlayerActivity
        val intent = Intent()
        intent.putExtra("user", currentMV.sysUser)
        intent.putExtra("addOrModifyMVSuccess", true)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onDeleteMVFailed(result: ResultBean) {
        myToast(getString(R.string.delete_mv_failed))
    }

    override fun onNetworkError() {
        myToast(getString(R.string.network_error))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_MV_REQUEST -> if (resultCode == RESULT_OK) {
                val addOrModifyMVSuccess =
                    data?.getBooleanExtra("addOrModifyMVSuccess", false)
                if (addOrModifyMVSuccess == true) {
                    val intent = Intent()
                    intent.putExtra("addOrModifyMVSuccess", true)
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        finish()
        super.onBackPressed()
    }

}