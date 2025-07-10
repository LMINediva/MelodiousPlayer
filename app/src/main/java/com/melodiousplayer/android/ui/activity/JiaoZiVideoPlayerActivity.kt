package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.VideoPagerAdapter
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.VideoPlayBean
import com.melodiousplayer.android.util.FileUtil
import com.melodiousplayer.android.util.URLProviderUtils

class JiaoZiVideoPlayerActivity : BaseActivity() {

    private lateinit var videoplayer: JzvdStd
    private lateinit var viewPager: ViewPager
    private lateinit var rg: RadioGroup
    private var data: Uri? = null
    private val permissions: Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private var hasPermissions: Boolean = true

    override fun getLayoutId(): Int {
        return R.layout.activity_video_player_jiaozi
    }

    override fun initData() {
        videoplayer = findViewById(R.id.jz_video)
        viewPager = findViewById(R.id.viewPager)
        rg = findViewById(R.id.rg)
        data = intent.data
        if (data == null) {
            // 获取传递的数据
            val videoPlayBean = intent.getParcelableExtra<VideoPlayBean>("item")
            // 从应用内响应视频播放
            videoplayer.setUp(
                URLProviderUtils.protocol + URLProviderUtils.serverAddress
                        + videoPlayBean?.url, videoPlayBean?.title, JzvdStd.SCREEN_NORMAL
            )
        } else {
            if (data.toString().startsWith("http")) {
                // 应用外的网络视频请求
                // 应用外响应
                videoplayer.setUp(data?.toString(), data.toString(), JzvdStd.SCREEN_NORMAL)
            } else {
                // 应用外的本地视频请求
                // 动态申请权限
                hasPermissions = hasPermissions(this, permissions)
                if (!hasPermissions) {
                    ActivityCompat.requestPermissions(this, permissions, 1)
                } else {
                    // 应用外响应
                    val filePath = FileUtil.getFileFromUri(data, this)?.absolutePath
                    videoplayer.setUp(filePath, filePath.toString(), JzvdStd.SCREEN_NORMAL)
                }
            }
        }
    }

    /**
     * 检查多个权限是否授权
     */
    fun hasPermissions(context: Context, permissions: Array<String>): Boolean {
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
                    videoplayer.setUp(filePath, filePath.toString(), JzvdStd.SCREEN_NORMAL)
                } else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show()
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
        viewPager.adapter = VideoPagerAdapter(supportFragmentManager)
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