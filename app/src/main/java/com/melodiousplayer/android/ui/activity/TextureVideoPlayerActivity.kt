package com.melodiousplayer.android.ui.activity

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.VideoPlayBean

class TextureVideoPlayerActivity : BaseActivity(), TextureView.SurfaceTextureListener {

    private lateinit var textureView: TextureView
    var videoPlayBean: VideoPlayBean? = null

    override fun getLayoutId(): Int {
        return R.layout.activity_video_player_texture
    }

    override fun initData() {
        // 获取传递的数据
        videoPlayBean = intent.getParcelableExtra<VideoPlayBean>("item")
        textureView = findViewById(R.id.textureView)
        textureView.surfaceTextureListener = this
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        videoPlayBean?.let {
            // 视图可用
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(it.url)
            // 设置播放视频画面
            mediaPlayer.setSurface(Surface(surface))
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                mediaPlayer.start()
            }
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        // view大小变化
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        // 视图销毁
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        // 视图更新
    }

}