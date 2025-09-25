package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.DeleteUploadMusicFileCacheContract
import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.DeleteUploadMusicFileCacheRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class DeleteUploadMusicFileCachePresenterImpl(val view: DeleteUploadMusicFileCacheContract.View) :
    DeleteUploadMusicFileCacheContract.Presenter, ResponseHandler<ResultBean> {

    override fun deleteUploadMusicFileCache(token: String, music: MusicBean) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(music)
        DeleteUploadMusicFileCacheRequest(this).executePostWithJSON(token, json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 删除成功
            view.onDeleteUploadMusicFileCacheSuccess(result)
        } else {
            // 删除失败
            view.onDeleteUploadMusicFileCacheFailed(result)
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}