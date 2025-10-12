package com.melodiousplayer.android.ui.fragment

import android.view.View
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment

/**
 * 我的音乐界面
 */
class MyMusicFragment : BaseFragment() {

    private lateinit var view: View

    override fun initView(): View? {
        if (!::view.isInitialized) {
            view = View.inflate(context, R.layout.fragment_my_music, null)
        }
        return view
    }

    override fun initData() {

    }

    override fun initListener() {

    }

    companion object {
        /**
         * 单例，返回给定编号的此片段的新实例
         */
        @JvmStatic
        fun newInstance(): MyMusicFragment {
            return MyMusicFragment().apply {

            }
        }
    }

}