package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.AddMusicContract
import com.melodiousplayer.android.extension.isValidArtistName
import com.melodiousplayer.android.extension.isValidDescription
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.AddMusicRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class AddMusicPresenterImpl(val view: AddMusicContract.View) :
    AddMusicContract.Presenter, ResponseHandler<ResultBean> {

    override fun addMusic(token: String, music: MusicBean) {
        // 校验歌手姓名
        if (music.artistName?.isValidArtistName() == true) {
            // 歌手姓名有效，继续校验音乐描述
            if (music.description?.isValidDescription() == true) {
                // 音乐描述有效，继续校验音乐海报图片
                if (!music.posterPic.isNullOrEmpty()) {
                    // 音乐海报图片不为空，继续校验音乐缩略图
                    if (!music.thumbnailPic.isNullOrEmpty()) {
                        // 音乐缩略图不为空，继续校验音乐文件
                        if (!music.url.isNullOrEmpty()) {
                            // 音乐文件不为空，开始添加音乐
                            addMusicRequest(token, music)
                        } else {
                            view.onMusicFileError()
                        }
                    } else {
                        view.onMusicThumbnailError()
                    }
                } else {
                    view.onMusicPosterError()
                }
            } else {
                view.onDescriptionError()
            }
        } else {
            view.onArtistNameError()
        }
    }

    private fun addMusicRequest(token: String, music: MusicBean) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(music)
        AddMusicRequest(this).executePostWithJSON(token = token, json = json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 更新成功
            view.onAddMusicSuccess()
        } else {
            // 更新失败
            view.onAddMusicFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}