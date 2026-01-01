package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.AddMVContract
import com.melodiousplayer.android.extension.isValidArtistName
import com.melodiousplayer.android.extension.isValidDescription
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.net.AddMVRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class AddMVPresenterImpl(val view: AddMVContract.View) :
    AddMVContract.Presenter, ResponseHandler<ResultBean> {

    override fun addMV(token: String, mv: VideosBean) {
        // 校验歌手姓名
        if (mv.artistName?.isValidArtistName() == true) {
            // 歌手姓名有效，继续校验MV描述
            if (mv.description?.isValidDescription() == true) {
                // MV描述有效，继续校验MV海报图片
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