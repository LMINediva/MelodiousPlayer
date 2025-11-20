package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.AddMusicListContract
import com.melodiousplayer.android.extension.isValidCategory
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.AddMusicListRequest
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

class AddMusicListPresenterImpl(val view: AddMusicListContract.View) :
    AddMusicListContract.Presenter, ResponseHandler<ResultBean> {

    private val client by lazy { OkHttpClient() }

    override fun addMusicList(token: String, play: PlayListsBean) {
        if (!play.title.isNullOrEmpty()) {
            // 悦单名不为空，检查悦单的id是否为空
            if (play.id == null) {
                // 悦单的id为空，检查悦单名是否存在
                if (checkMusicListTitleRequest(token, play.title)) {
                    // 悦单名不存在，继续校验剩余项
                    verificationMusicList(token, play)
                }
            } else {
                // 悦单的id不为空，继续校验剩余项
                verificationMusicList(token, play)
            }
        } else {
            view.onMusicListTitleError()
        }
    }

    private fun checkMusicListTitleRequest(token: String, playTitle: String?): Boolean {
        val play = PlayListsBean(
            null, playTitle, null, null,
            null, null, null, null,
            null, null, null, null,
            null, null, null, null, null
        )
        val json = Gson().toJson(play)
        val requestBody: RequestBody =
            RequestBody.create("application/json;charset=utf-8".toMediaType(), json)
        val request = Request.Builder()
            .url(URLProviderUtils.postCheckMusicListTitle())
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
                val playTitleResult = gson.fromJson(
                    result,
                    ResultBean::class.java
                )
                if (playTitleResult.code == 200) {
                    isValidMVTitle = true
                } else {
                    isValidMVTitle = false
                    ThreadUtil.runOnMainThread(object : Runnable {
                        override fun run() {
                            view.onMusicListTitleExistError()
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

    private fun verificationMusicList(token: String, play: PlayListsBean) {
        if (!play.description.isNullOrEmpty()) {
            // 悦单描述不为空，继续校验悦单类型是否在规定大小范围中
            if (play.category!!.isValidCategory()) {
                // 悦单类型字符数在规定大小范围中，继续校验悦单缩略图
                if (!play.thumbnailPic.isNullOrEmpty()) {
                    // 悦单缩略图不为空，继续校验悦单中MV数量是否在规定大小范围中
                    if (play.mvList!!.size in 3..50) {
                        // 悦单中MV数量在规定大小范围中，开始添加悦单
                        addMusicListRequest(token, play)
                    } else {
                        view.onMusicListMVQuantityError()
                    }
                } else {
                    view.onMusicListThumbnailError()
                }
            } else {
                view.onCategoryError()
            }
        } else {
            view.onDescriptionError()
        }
    }

    private fun addMusicListRequest(token: String, play: PlayListsBean) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(play)
        AddMusicListRequest(this).executePostWithJSON(token = token, json = json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 更新成功
            view.onAddMusicListSuccess()
        } else {
            // 更新失败
            view.onAddMusicListFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}