package com.melodiousplayer.android.service

interface IService {
    fun updatePlayState()
    fun isPlaying(): Boolean?
    fun getDuration(): Int
    fun getProgress(): Int
}