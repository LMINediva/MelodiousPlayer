package com.melodiousplayer.android.presenter.impl

import com.melodiousplayer.android.contract.GetLyricTextContract
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.GetLyricTextRequest
import com.melodiousplayer.android.net.ResponseHandler

class GetLyricTextPresenterImpl(val view: GetLyricTextContract.View) :
    GetLyricTextContract.Presenter, ResponseHandler<ResultBean> {

    override fun getLyricText(lyricFileName: String) {
        GetLyricTextRequest(lyricFileName, this).executeGetText()
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 获取歌词文本成功
            view.onGetLyricTextSuccess(result)
        } else {
            // 获取歌词文本失败
            view.onGetLyricTextFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}