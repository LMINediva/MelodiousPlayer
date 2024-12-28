package com.melodiousplayer.android.ui.fragment

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.base.BaseFragment

/**
 * 首页
 */
class HomeFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView

    override fun initView(): View? {
        return View.inflate(context, R.layout.fragment_home, null)
    }

    override fun initListener() {
        // 初始化RecyclerView
    }

}