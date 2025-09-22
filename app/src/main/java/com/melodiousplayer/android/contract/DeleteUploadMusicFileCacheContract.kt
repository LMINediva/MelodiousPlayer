package com.melodiousplayer.android.contract

import com.melodiousplayer.android.model.MusicBean
import com.melodiousplayer.android.model.ResultBean

/**
 * 删除上传到服务器的音乐相关文件缓存协议持久化接口
 */
interface DeleteUploadMusicFileCacheContract {

    interface Presenter : BasePresenter {
        fun deleteUploadMusicFileCache(token: String, music: MusicBean)
    }

    interface View {
        fun onDeleteUploadMusicFileCacheSuccess(result: ResultBean)
        fun onDeleteUploadMusicFileCacheFailed(result: ResultBean)
        fun onNetworkError()
    }

}