package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.TitleContract
import com.melodiousplayer.android.extension.isValidTitle
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.ResponseHandler
import com.melodiousplayer.android.net.TitleRequest

class TitlePresenterImpl(val view: TitleContract.View) : TitleContract.Presenter,
    ResponseHandler<ResultBean> {

    override fun checkTitle(token: String, url: String, title: String, json: String) {
        if (title.isValidTitle()) {
            TitleRequest(url, this).executePostWithJSON(token = token, json = json)
        } else {
            view.onTitleError()
        }
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 标题不重复
            view.onCheckTitleSuccess()
        } else if (result.code == 500) {
            // 标题重复
            view.onCheckTitleFailed(result)
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}