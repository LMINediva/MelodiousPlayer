package com.melodiousplayer.android.ui.fragment

import android.content.AsyncQueryHandler
import android.database.Cursor
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
        val handler = object : AsyncQueryHandler(resolver) {
            override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
                // 查询完成回调，主线程中
                // 打印数据
                CursorUtil.logCursor(cursor)
            }
        }
        // 开始查询
        handler.startQuery(
            0, null,
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                Media.DATA,
                Media.SIZE,
                Media.DISPLAY_NAME,
                Media.ARTIST
            ),
            null, null, null
        )
    }

}