package com.melodiousplayer.android.ui.fragment

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.MusicListAdapter
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.model.MusicListBean
import com.melodiousplayer.android.presenter.impl.MusicListPresenterImpl
import com.melodiousplayer.android.view.MusicListView

/**
 * 悦单界面
 */
class MusicListFragment : BaseFragment(), MusicListView {

    val adapter by lazy { MusicListAdapter() }

    val presenter by lazy { MusicListPresenterImpl(this) }

    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun initView(): View? {
        val view = View.inflate(context, R.layout.fragment_list, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        refreshLayout = view.findViewById(R.id.refreshLayout)
        return view
    }

    override fun initListener() {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        // 初始化刷新控件
        refreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN)
        // 监听刷新控件
        refreshLayout.setOnRefreshListener {
            presenter.loadDatas()
        }
        // 监听列表滑动
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 空闲状态，判断最后可见条目，是否是列表的最后一条
                    val layoutManager = recyclerView.layoutManager
                    if (!(layoutManager is LinearLayoutManager)) return
                    // 显示列表，kotlin智能类型转换
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if (lastPosition == adapter.itemCount - 1) {
                        // 加载更多已经显示
                        presenter.loadMore(adapter.itemCount - 1)
                    }
                }
            }
        })
    }

    override fun initData() {
        // 加载数据
        presenter.loadDatas()
    }

    override fun onError(message: String?) {
        // 隐藏刷新控件
        refreshLayout.isRefreshing = false
        myToast("加载数据失败")
    }

    override fun loadSuccess(response: MusicListBean?) {
        myToast("加载数据成功")
        // 隐藏刷新控件
        refreshLayout.isRefreshing = false
        // 刷新adapter
        adapter.updateList(response?.playLists)
    }

    override fun loadMore(response: MusicListBean?) {
        // 刷新列表
        adapter.loadMore(response?.playLists)
    }

}