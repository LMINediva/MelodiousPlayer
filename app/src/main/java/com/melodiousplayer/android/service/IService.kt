package com.melodiousplayer.android.service

import com.melodiousplayer.android.model.AudioBean

interface IService {
    fun updatePlayState()
    fun isPlaying(): Boolean?
    fun getDuration(): Int
    fun getProgress(): Int
    fun seekTo(progress: Int)
    fun updatePlayMode()
    fun getPlayMode(): Int
    fun playPrevious()
    fun playNext()
    fun getPlayList(): List<AudioBean>?
    fun playPosition(position: Int)
}