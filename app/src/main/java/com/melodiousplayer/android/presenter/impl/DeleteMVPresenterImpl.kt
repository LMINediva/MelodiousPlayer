package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.DeleteMVContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.DeleteMVRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class DeleteMVPresenterImpl(val view: DeleteMVContract.View) :
    DeleteMVContract.Presenter, ResponseHandler<ResultBean> {

    override fun deleteMV(token: String, ids: Array<Int>) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(ids)
        DeleteMVRequest(this).executePostWithJSON(token, json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 删除成功
            view.onDeleteMVSuccess(result)
        } else {
            // 删除失败
            view.onDeleteMVFailed(result)
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}