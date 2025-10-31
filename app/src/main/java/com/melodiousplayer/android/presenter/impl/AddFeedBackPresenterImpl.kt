package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.AddFeedBackContract
import com.melodiousplayer.android.extension.isValidFeedBackContent
import com.melodiousplayer.android.model.FeedBackBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.AddFeedBackRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class AddFeedBackPresenterImpl(val view: AddFeedBackContract.View) :
    AddFeedBackContract.Presenter, ResponseHandler<ResultBean> {

    override fun addFeedBack(token: String, feedback: FeedBackBean) {
        if (!feedback.content.isNullOrEmpty()) {
            // 用户反馈内容不为空，继续校验用户反馈内容长度是否在规定范围内
            if (feedback.content!!.isValidFeedBackContent()) {
                // 用户反馈内容长度在规定范围内，开始添加用户反馈
                addFeedBackRequest(token, feedback)
            } else {
                view.onFeedBackContentLengthError()
            }
        } else {
            view.onFeedBackContentError()
        }
    }

    private fun addFeedBackRequest(token: String, feedback: FeedBackBean) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(feedback)
        AddFeedBackRequest(this).executePostWithJSON(token = token, json = json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 更新成功
            view.onAddFeedBackSuccess()
        } else {
            // 更新失败
            view.onAddFeedBackFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}