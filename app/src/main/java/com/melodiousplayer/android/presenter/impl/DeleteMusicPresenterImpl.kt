package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.DeleteMusicContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.DeleteMusicRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class DeleteMusicPresenterImpl(val view: DeleteMusicContract.View) :
    DeleteMusicContract.Presenter, ResponseHandler<ResultBean> {

    override fun deleteMusic(token: String, ids: Array<Int>) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(ids)
        DeleteMusicRequest(this).executePostWithJSON(token, json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 删除成功
            view.onDeleteMusicSuccess(result)
        } else {
            // 删除失败
            view.onDeleteMusicFailed(result)
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}