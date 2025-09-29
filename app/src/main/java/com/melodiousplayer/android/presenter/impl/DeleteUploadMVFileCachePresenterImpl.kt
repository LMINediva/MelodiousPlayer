package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.DeleteUploadMVFileCacheContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.VideosBean
import com.melodiousplayer.android.net.DeleteUploadMVFileCacheRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class DeleteUploadMVFileCachePresenterImpl(val view: DeleteUploadMVFileCacheContract.View) :
    DeleteUploadMVFileCacheContract.Presenter, ResponseHandler<ResultBean> {

    override fun deleteUploadMVFileCache(token: String, mv: VideosBean) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(mv)
        DeleteUploadMVFileCacheRequest(this).executePostWithJSON(token, json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 删除成功
            view.onDeleteUploadMVFileCacheSuccess(result)
        } else {
            // 删除失败
            view.onDeleteUploadMVFileCacheFailed(result)
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}