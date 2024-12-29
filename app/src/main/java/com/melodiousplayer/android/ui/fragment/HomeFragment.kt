package com.melodiousplayer.android.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.HomeAdapter
import com.melodiousplayer.android.base.BaseFragment

/**
 * 首页
 */
class HomeFragment : BaseFragment() {

    private lateinit var recyclerView: RecyclerView

    override fun initView(): View? {
        val view = View.inflate(context, R.layout.fragment_home, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun initListener() {
        // 初始化RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        // 适配
        val adapter = HomeAdapter()
        recyclerView.adapter = adapter
    }

}