package com.melodiousplayer.android.ui.activity

import android.Manifest
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
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1
                )
            } else {
                // 应用外响应
                videoplayer.setUp(data.toString(), data.toString(), JzvdStd.SCREEN_NORMAL)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 ->
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    // 应用外响应
                    videoplayer.setUp(data.toString(), data.toString(), JzvdStd.SCREEN_NORMAL)
                } else {
                    Toast.makeText(this, "你拒绝了权限", Toast.LENGTH_SHORT).show()
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