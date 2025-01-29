package com.melodiousplayer.android.ui.activity

import android.media.MediaPlayer
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseActivity
import com.melodiousplayer.android.model.AudioBean

/**
 * 音乐播放界面
 */
class AudioPlayerActivity : BaseActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_audio_player
    }

    override fun initData() {
        val list = intent.getParcelableArrayListExtra<AudioBean>("list")
        val position = intent.getIntExtra("position", -1)
        // 播放音乐
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setOnPreparedListener {
            // 开始播放
            mediaPlayer.start()
        }
        mediaPlayer.setDataSource(list?.get(position)?.data)
        mediaPlayer.prepareAsync()
    }

}