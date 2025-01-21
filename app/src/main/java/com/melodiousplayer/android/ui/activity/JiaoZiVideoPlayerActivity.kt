package com.melodiousplayer.android.ui.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.VideoPlayBean

class JiaoZiVideoPlayerActivity : BaseActivity() {

    private lateinit var videoplayer: JzvdStd
    private var data: String? = null
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
        data = intent.data?.path
        if (data == null) {
            // 获取传递的数据
            val videoPlayBean = intent.getParcelableExtra<VideoPlayBean>("item")
            // 从应用内响应视频播放
            videoplayer.setUp(videoPlayBean?.url, videoPlayBean?.title, JzvdStd.SCREEN_NORMAL)
        } else {
            // 动态申请权限
            hasPermissions = hasPermissions(this, permissions)
            if (!hasPermissions) {
                ActivityCompat.requestPermissions(this, permissions, 1)
            } else {
                // 应用外响应
                videoplayer.setUp(data.toString(), data.toString(), JzvdStd.SCREEN_NORMAL)
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
                    videoplayer.setUp(data.toString(), data.toString(), JzvdStd.SCREEN_NORMAL)
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

}