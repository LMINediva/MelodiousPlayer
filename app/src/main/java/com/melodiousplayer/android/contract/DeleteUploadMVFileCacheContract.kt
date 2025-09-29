package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.ResultBean
import com.melodiousplayer.android.model.VideosBean

/**
 * 删除上传到服务器的MV相关文件缓存协议持久化接口
 */
interface DeleteUploadMVFileCacheContract {

    interface Presenter : BasePresenter {
        fun deleteUploadMVFileCache(token: String, mv: VideosBean)
    }

    interface View {
        fun onDeleteUploadMVFileCacheSuccess(result: ResultBean)
        fun onDeleteUploadMVFileCacheFailed(result: ResultBean)
        fun onNetworkError()
    }

}