package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.melodiousplayer.android.contract.GetMVListContract
import com.melodiousplayer.android.model.MVListResultBean
import com.melodiousplayer.android.model.PageBean
import com.melodiousplayer.android.net.MVListTokenRequest
import com.melodiousplayer.android.net.ResponseHandler

class GetMVListPresenterImpl(val view: GetMVListContract.View) : GetMVListContract.Presenter,
    ResponseHandler<MVListResultBean> {

    override fun getMVList(token: String, pageBean: PageBean) {
        val json = Gson().toJson(pageBean)
        MVListTokenRequest(this).executePostWithJSON(token = token, json = json)
    }

    override fun onSuccess(type: Int, result: MVListResultBean) {
        if (result.code == 200) {
            view.onGetMVListSuccess(result)
        } else {
            view.onGetMVListFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }


}