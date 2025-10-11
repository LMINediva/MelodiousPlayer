package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.DeleteUploadMusicListFileCacheContract
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.DeleteUploadMusicListFileCacheRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class DeleteUploadMusicListFileCachePresenterImpl(val view: DeleteUploadMusicListFileCacheContract.View) :
    DeleteUploadMusicListFileCacheContract.Presenter, ResponseHandler<ResultBean> {

    override fun deleteUploadMusicListFileCache(token: String, play: PlayListsBean) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(play)
        DeleteUploadMusicListFileCacheRequest(this).executePostWithJSON(token, json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 删除成功
            view.onDeleteUploadMusicListFileCacheSuccess(result)
        } else {
            // 删除失败
            view.onDeleteUploadMusicListFileCacheFailed(result)
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}