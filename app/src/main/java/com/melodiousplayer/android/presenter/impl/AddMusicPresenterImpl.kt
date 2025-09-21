package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.melodiousplayer.android.contract.AddMusicContract
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.AddMusicRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.util.URLProviderUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class AddMusicPresenterImpl(val view: AddMusicContract.View) :
    AddMusicContract.Presenter, ResponseHandler<ResultBean> {

    private val client by lazy { OkHttpClient() }

    override fun addMusic(token: String, music: MusicBean) {
        if (music.title.isNullOrEmpty()) {
            // 音乐名不为空，检查音乐名是否存在
            if (checkMusicTitleRequest(token, music.title)) {
                // 音乐名不存在，继续校验歌手姓名
                if (music.artistName.isNullOrEmpty()) {
                    // 歌手姓名不为空，继续校验音乐描述
                    if (music.description.isNullOrEmpty()) {
                        // 音乐描述不为空，开始添加音乐
                        addMusicRequest(token, music)
                    } else {
                        view.onDescriptionError()
                    }
                } else {
                    view.onArtistNameError()
                }
            }
        } else {
            view.onMusicTitleError()
        }
    }

    private fun checkMusicTitleRequest(token: String, musicTitle: String?): Boolean {
        val music = MusicBean(
            null, null, musicTitle, null,
            null, null, null, null,
            null, null, null, null, null,
            null, null, null
        )
        val json = Gson().toJson(music)
        val requestBody: RequestBody =
            RequestBody.create("application/json;charset=utf-8".toMediaType(), json)
        val request = Request.Builder()
            .url(URLProviderUtils.postCheckMusicTitle())
            .post(requestBody)
            .addHeader("token", token)
            .build()

        var isValidMusicTitle = false
        client.newCall(request).enqueue(object : Callback {
            /**
             * 请求成功，子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val gson = Gson()
                val musicTitleResult = gson.fromJson(
                    result,
                    ResultBean::class.java
                )
                if (musicTitleResult.code == 200) {
                    isValidMusicTitle = true
                } else {
                    isValidMusicTitle = false
                    view.onMusicTitleExistError()
                }
            }

            /**
             * 请求失败，子线程中调用
             */
            override fun onFailure(call: Call, e: IOException) {
                isValidMusicTitle = false
                // 回调到view层处理
                view.onNetworkError()
            }
        })
        // 延时500ms，确保isValidMusicTitle变量更新成功
        runBlocking {
            delay(500)
        }
        return isValidMusicTitle
    }

    private fun addMusicRequest(token: String, music: MusicBean) {
        val json = Gson().toJson(music)
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