package com.melodiousplayer.android.ui.activity

import android.widget.VideoView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.VideoPlayBean

class VideoPlayerActivity : BaseActivity() {

    private lateinit var videoView: VideoView

    override fun getLayoutId(): Int {
        return R.layout.activity_video_player
    }

    override fun initData() {
        // 获取传递的数据
        val videoPlayBean = intent.getParcelableExtra<VideoPlayBean>("item")
        videoView = findViewById(R.id.videoView)
        // 异步准备
        videoView.setVideoPath(videoPlayBean?.url)
        videoView.setOnPreparedListener {
            videoView.start()
        }
    }

}