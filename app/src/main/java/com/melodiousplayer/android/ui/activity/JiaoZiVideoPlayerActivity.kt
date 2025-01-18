package com.melodiousplayer.android.ui.activity

import cn.jzvd.Jzvd
import cn.jzvd.JzvdStd
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.VideoPlayBean

class JiaoZiVideoPlayerActivity : BaseActivity() {

    private lateinit var videoplayer: JzvdStd

    override fun getLayoutId(): Int {
        return R.layout.activity_video_player_jiaozi
    }

    override fun initData() {
        // 获取传递的数据
        val videoPlayBean = intent.getParcelableExtra<VideoPlayBean>("item")
        videoplayer = findViewById(R.id.jz_video)
        videoplayer.setUp("http://192.168.124.10/mp4/01.mp4", "这个年纪")
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