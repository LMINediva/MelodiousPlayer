package com.melodiousplayer.android.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.MVListAdapter
import com.melodiousplayer.android.base.BaseFragment

/**
 * 悦单界面
 */
class MVListFragment : BaseFragment() {

    val adapter by lazy { MVListAdapter() }

    private lateinit var recyclerView: RecyclerView

    override fun initView(): View? {
        val view = View.inflate(context, R.layout.fragment_list, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun initListener() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }

}