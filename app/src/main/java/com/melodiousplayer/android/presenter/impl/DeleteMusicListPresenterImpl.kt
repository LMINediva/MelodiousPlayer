package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.DeleteMusicListContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.DeleteMusicListRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class DeleteMusicListPresenterImpl(val view: DeleteMusicListContract.View) :
    DeleteMusicListContract.Presenter, ResponseHandler<ResultBean> {

    override fun deleteMusicList(token: String, ids: Array<Int>) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(ids)
        DeleteMusicListRequest(this).executePostWithJSON(token, json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 删除成功
            view.onDeleteMusicListSuccess(result)
        } else {
            // 删除失败
            view.onDeleteMusicListFailed(result)
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}