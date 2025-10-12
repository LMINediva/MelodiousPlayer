package com.melodiousplayer.android.ui.fragment

import android.view.View
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment

/**
 * 我的MV界面
 */
class MyMVFragment : BaseFragment() {

    private lateinit var view: View

    override fun initView(): View? {
        if (!::view.isInitialized) {
            view = View.inflate(context, R.layout.fragment_my_mv, null)
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
        fun newInstance(): MyMVFragment {
            return MyMVFragment().apply {

            }
        }
    }

}