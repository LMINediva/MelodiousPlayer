package com.melodiousplayer.android.ui.fragment

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.HomeAdapter
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.presenter.impl.HomePresenterImpl
import com.melodiousplayer.android.view.HomeView

/**
 * 首页
 */
class HomeFragment : BaseFragment(), HomeView {

    // 适配
    val adapter by lazy { HomeAdapter() }
    val presenter by lazy { HomePresenterImpl(this) }

    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun initView(): View? {
        val view = View.inflate(context, R.layout.fragment_list, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        refreshLayout = view.findViewById(R.id.refreshLayout)
        return view
    }

    override fun initListener() {
        // 初始化RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        // 初始化刷新控件
        refreshLayout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN)
        // 刷新监听
        refreshLayout.setOnRefreshListener {
            presenter.loadDatas()
        }
        // 监听列表滑动
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                /*when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        println("idel")
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        println("drag")
                    }

                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        println("settling")
                    }
                }*/
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 最后一条是否已经显示
                    val layoutManager = recyclerView.layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        val manager: LinearLayoutManager = layoutManager
                        val lastPosition = manager.findLastVisibleItemPosition()
                        if (lastPosition == adapter.itemCount - 1) {
                            // 最后一条已经显示了
                            presenter.loadMore(adapter.itemCount - 1)
                        }
                    }
                }
            }

            /*override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                println("onScrolled dx=$dx dy=$dy")
            }*/
        })
    }

    override fun initData() {
        // 初始化数据
        presenter.loadDatas()
    }

    override fun onError(message: String?) {
        myToast("加载数据失败")
    }

    override fun loadSuccess(list: List<HomeItemBean>?) {
        // 隐藏刷新控件
        refreshLayout.isRefreshing = false
        // 刷新列表
        adapter.updateList(list)
    }

    override fun loadMore(list: List<HomeItemBean>?) {
        adapter.loadMoreList(list)
    }

}