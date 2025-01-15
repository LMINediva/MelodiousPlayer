package com.melodiousplayer.android.ui.activity

import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.VideoPlayBean

class VideoPlayerActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_video_player
    }

    override fun initData() {
        // 获取传递的数据
        val videoPlayBean = intent.getParcelableExtra<VideoPlayBean>("item")
        println("itemBean=$videoPlayBean")
    }

}