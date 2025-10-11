package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.PlayListsBean
import com.melodiousplayer.android.model.ResultBean

/**
 * 删除上传到服务器的悦单相关文件缓存协议持久化接口
 */
interface DeleteUploadMusicListFileCacheContract {

    interface Presenter : BasePresenter {
        fun deleteUploadMusicListFileCache(token: String, play: PlayListsBean)
    }

    interface View {
        fun onDeleteUploadMusicListFileCacheSuccess(result: ResultBean)
        fun onDeleteUploadMusicListFileCacheFailed(result: ResultBean)
        fun onNetworkError()
    }

}