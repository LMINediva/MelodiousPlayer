package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.widget.RadioGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.VideoPagerAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.VideoPlayBean
import com.melodiousplayer.android.util.FileUtil
import com.melodiousplayer.android.util.FileUtil.createTemporalFileFrom
import com.melodiousplayer.android.util.URLProviderUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

class JiaoZiVideoPlayerActivity : BaseActivity() {

    private lateinit var videoPlayer: JzvdStd
    private lateinit var viewPager: ViewPager
    private lateinit var rg: RadioGroup
    private var data: Uri? = null
    private val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var hasPermissions: Boolean = true
    private val client by lazy { OkHttpClient() }
    private var videoPlayBean: VideoPlayBean? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_video_player_jiaozi
    }

    override fun initData() {
        videoPlayer = findViewById(R.id.jz_video)
        viewPager = findViewById(R.id.viewPager)
        rg = findViewById(R.id.rg)
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

    override fun onBackPressed() {
        if (Jzvd.backPress()) {
            return
        }
        super.onBackPressed()
    }

    override fun onPause() {
        super.onPause()
        Jzvd.releaseAllVideos()
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

}