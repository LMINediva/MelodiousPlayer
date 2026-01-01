package com.melodiousplayer.android.presenter.impl

import com.google.gson.GsonBuilder
import com.melodiousplayer.android.adapter.DateTypeAdapter
import com.melodiousplayer.android.contract.AddMusicListContract
import com.melodiousplayer.android.extension.isValidCategory
import com.melodiousplayer.android.extension.isValidDescription
import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.net.AddMusicListRequest
import com.melodiousplayer.android.net.ResponseHandler
import java.util.Date

class AddMusicListPresenterImpl(val view: AddMusicListContract.View) :
    AddMusicListContract.Presenter, ResponseHandler<ResultBean> {

    override fun addMusicList(token: String, play: PlayListsBean) {
        if (play.description?.isValidDescription() == true) {
            // 悦单描述不为空，继续校验悦单类型是否在规定大小范围中
            if (play.category!!.isValidCategory()) {
                // 悦单类型字符数在规定大小范围中，继续校验悦单缩略图
                if (!play.thumbnailPic.isNullOrEmpty()) {
                    // 悦单缩略图不为空，继续校验悦单中MV数量是否在规定大小范围中
                    if (play.mvList!!.size in 3..50) {
                        // 悦单中MV数量在规定大小范围中，开始添加悦单
                        addMusicListRequest(token, play)
                    } else {
                        view.onMusicListMVQuantityError()
                    }
                } else {
                    view.onMusicListThumbnailError()
                }
            } else {
                view.onCategoryError()
            }
        } else {
            view.onDescriptionError()
        }
    }

    private fun addMusicListRequest(token: String, play: PlayListsBean) {
        val gson = GsonBuilder()
            .registerTypeAdapter(Date::class.java, DateTypeAdapter())
            .create()
        val json = gson.toJson(play)
        AddMusicListRequest(this).executePostWithJSON(token = token, json = json)
    }

    override fun onSuccess(type: Int, result: ResultBean) {
        if (result.code == 200) {
            // 更新成功
            view.onAddMusicListSuccess()
        } else {
            // 更新失败
            view.onAddMusicListFailed()
        }
    }

    override fun onError(type: Int, msg: String?) {
        // 网络错误
        view.onNetworkError()
    }

}