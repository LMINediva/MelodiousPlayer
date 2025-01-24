package com.melodiousplayer.android.ui.fragment

import android.content.ContentResolver
import android.database.Cursor
import android.os.AsyncTask
import android.provider.MediaStore.Audio.Media
import android.view.View
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.util.CursorUtil

/**
 * 本地音乐
 */
class LocalMusicFragment : BaseFragment() {

    override fun initView(): View? {
        return View.inflate(context, R.layout.fragment_local_music, null)
    }

    override fun initData() {
        // 加载音乐列表数据
        val resolver = context?.contentResolver
        // AsyncTask
        AudioTask().execute(resolver)
    }

    /**
     * 音乐查询异步任务
     */
    class AudioTask : AsyncTask<ContentResolver, Void, Cursor>() {

        /**
         * 后台执行任务，在新线程中执行
         */
        override fun doInBackground(vararg params: ContentResolver?): Cursor? {
            val cursor = params[0]?.query(
                Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    Media.DATA,
                    Media.SIZE,
                    Media.DISPLAY_NAME,
                    Media.ARTIST
                ),
                null, null, null
            )
            return cursor
        }

        /**
         * 将后台任务结果回调到主线程中
         */
        override fun onPostExecute(result: Cursor?) {
            super.onPostExecute(result)
            // 打印Cursor
            CursorUtil.logCursor(result)
        }

    }

}