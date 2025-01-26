package com.melodiousplayer.android.ui.fragment

import android.content.AsyncQueryHandler
import android.database.Cursor
import android.provider.MediaStore.Audio.Media
import android.view.View
import android.widget.ListView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.LocalMusicAdapter
import com.melodiousplayer.android.base.BaseFragment

/**
 * 本地音乐
 */
class LocalMusicFragment : BaseFragment() {

    var adapter: LocalMusicAdapter? = null
    private lateinit var view: View
    private lateinit var listView: ListView

    override fun initView(): View? {
        if (!::view.isInitialized) {
            view = View.inflate(context, R.layout.fragment_local_music, null)
        }
        return view
    }

    override fun initData() {
        listView = view.findViewById(R.id.listView)
        adapter = LocalMusicAdapter(context, null)
        // 加载音乐列表数据
        val resolver = context?.contentResolver
        val handler = object : AsyncQueryHandler(resolver) {
            override fun onQueryComplete(token: Int, cookie: Any?, cursor: Cursor?) {
                // 查询完成回调，主线程中
                // 设置数据源，刷新adapter
                (cookie as LocalMusicAdapter).swapCursor(cursor)
            }
        }
        // 开始查询
        handler.startQuery(
            0, adapter,
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(
                Media._ID,
                Media.DATA,
                Media.SIZE,
                Media.DISPLAY_NAME,
                Media.ARTIST
            ),
            null, null, null
        )
    }

    override fun initListener() {
        listView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        // 界面销毁，关闭cursor
        // 获取adapter中的cursor，然后进行关闭，将adapter中的cursor设置为null
        adapter?.changeCursor(null)
    }

}