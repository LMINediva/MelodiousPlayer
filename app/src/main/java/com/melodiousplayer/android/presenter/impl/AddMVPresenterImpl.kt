package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.AddMVContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.net.AddMVRequest
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.util.ThreadUtil
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
import java.util.Date

class AddMVPresenterImpl(val view: AddMVContract.View) :
    AddMVContract.Presenter, ResponseHandler<ResultBean> {

    private val client by lazy { OkHttpClient() }

    override fun addMV(token: String, mv: VideosBean) {
        if (!mv.title.isNullOrEmpty()) {
            // MV名不为空，检查MV的id是否为空
            if (mv.id == null) {
                // MV的id为空，检查MV名是否存在
                if (checkMVTitleRequest(token, mv.title)) {
                    // MV名不存在，继续校验剩余项
                    verificationMV(token, mv)
                }
            } else {
                // MV的id不为空，继续校验剩余项
                verificationMV(token, mv)
            }
        } else {
            view.onMVTitleError()
        }
    }

    private fun checkMVTitleRequest(token: String, mvTitle: String?): Boolean {
        val mv = VideosBean(
            null, mvTitle, null, null, null,
            null, null, null, null,
            null, null, null, null,
            null, null, null, null, null,
            null, null, null, null
        )
        val json = Gson().toJson(mv)
        val requestBody: RequestBody =
            RequestBody.create("application/json;charset=utf-8".toMediaType(), json)
        val request = Request.Builder()
            .url(URLProviderUtils.postCheckMVTitle())
            .post(requestBody)
            .addHeader("token", token)
            .build()

        var isValidMVTitle = false
        client.newCall(request).enqueue(object : Callback {
            /**
             * 请求成功，子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val gson = Gson()
                val mvTitleResult = gson.fromJson(
                    result,
                    ResultBean::class.java
                )
                if (mvTitleResult.code == 200) {
                    isValidMVTitle = true
                } else {
                    isValidMVTitle = false
                    ThreadUtil.runOnMainThread(object : Runnable {
                        override fun run() {
                            view.onMVTitleExistError()
                        }
                    })
                }
            }

            /**
             * 请求失败，子线程中调用
             */
            override fun onFailure(call: Call, e: IOException) {
                isValidMVTitle = false
                // 回调到view层处理
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        view.onNetworkError()
                    }
                })
            }
        })
        // 延时500ms，确保isValidMVTitle变量更新成功
        runBlocking {
            delay(500)
        }
        return isValidMVTitle
    }

    private fun verificationMV(token: String, mv: VideosBean) {
        if (!mv.artistName.isNullOrEmpty()) {
            // 歌手姓名不为空，继续校验MV描述
            if (!mv.description.isNullOrEmpty()) {
                // MV描述不为空，继续校验MV海报图片
                if (!mv.posterPic.isNullOrEmpty()) {
                    // MV海报图片不为空，继续校验MV缩略图
                    if (!mv.thumbnailPic.isNullOrEmpty()) {
                        // MV缩略图不为空，继续校验MV文件
                        if (!mv.url.isNullOrEmpty()) {
                            // MV文件不为空，开始添加MV
                            addMVRequest(token, mv)
                        } else {
                            view.onMVFileError()
                        }
                    } else {
                        view.onMVThumbnailError()
                    }
                } else {
                    view.onMVPosterError()
                }
            } else {
                view.onDescriptionError()
            }
        } else {
            view.onArtistNameError()
        }
    }

    private fun addMVRequest(token: String, mv: VideosBean) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(mv)
        AddMVRequest(this).executePostWithJSON(token = token, json = json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 更新成功
            view.onAddMVSuccess()
        } else {
            // 更新失败
            view.onAddMVFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}