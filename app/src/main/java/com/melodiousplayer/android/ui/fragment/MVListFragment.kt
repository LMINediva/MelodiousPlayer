package com.melodiousplayer.android.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.MVListAdapter
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.model.MVListBean
import com.melodiousplayer.android.presenter.impl.MVListPresenterImpl
import com.melodiousplayer.android.view.MVListView

/**
 * 悦单界面
 */
class MVListFragment : BaseFragment(), MVListView {

    val adapter by lazy { MVListAdapter() }

    val presenter by lazy { MVListPresenterImpl(this) }

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

    override fun initData() {
        // 加载数据
        presenter.loadDatas()
    }

    override fun onError(message: String?) {
        myToast("加载数据失败")
    }

    override fun loadSuccess(response: MVListBean?) {
        // 刷新adapter
        adapter.updateList(response?.playLists)
    }

    override fun loadMore(response: MVListBean?) {
    }

}