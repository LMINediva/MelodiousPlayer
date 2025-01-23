package com.melodiousplayer.android.ui.fragment

import android.view.View
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment

/**
 * 本地音乐
 */
class LocalMusicFragment : BaseFragment() {

    override fun initView(): View? {
        return View.inflate(context, R.layout.fragment_local_music, null)
    }

    override fun initData() {
        // 加载音乐列表数据
    }

}