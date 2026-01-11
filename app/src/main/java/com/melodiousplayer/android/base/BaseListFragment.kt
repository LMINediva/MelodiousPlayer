package com.melodiousplayer.android.base

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.melodiousplayer.android.R

/**
 * 所有具有下拉刷新和上拉加载更多列表界面的基类
 * 基类抽取
 * HomeView -> BaseView
 * Adapter -> BaseListAdapter
 * Presenter -> BaseListPresenter
 */
abstract class BaseListFragment<RESPONSE, ITEMBEAN, ITEMVIEW : View> : BaseFragment(),
    BaseView<RESPONSE>, OnDataChangedListener {

    // 适配
    val adapter by lazy { getSpecialAdapter() }
    val presenter by lazy { getSpecialPresenter() }

    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout
    private lateinit var loadingLayout: RelativeLayout
    private var listener: MessageListener? = null

    override fun initView(): View? {
        val view = View.inflate(context, R.layout.fragment_list, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        refreshLayout = view.findViewById(R.id.refreshLayout)
        loadingLayout = view.findViewById(R.id.loadingLayout)
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
                            if (((adapter.itemCount - 1) / 20) == 0) {
                                presenter.loadMore(2)
                            } else {
                                if (((adapter.itemCount - 1) % 20) == 0) {
                                    presenter.loadMore((adapter.itemCount - 1) / 20 + 1)
                                } else {
                                    presenter.loadMore((adapter.itemCount - 1) / 20 + 2)
                                }
                            }
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
        sendMessage("error")
    }

    override fun loadSuccess(response: RESPONSE?) {
        // 隐藏刷新控件
        refreshLayout.isRefreshing = false
        refreshLayout.visibility = View.VISIBLE
        loadingLayout.visibility = View.GONE
        // 刷新列表
        adapter.updateList(getList(response))
    }

    override fun loadMore(response: RESPONSE?) {
        adapter.loadMoreList(getList(response))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? MessageListener
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun sendMessage(message: String) {
        listener?.onMessageReceived(message)
    }

    override fun onDataChanged() {
        presenter.loadDatas()
    }

    /**
     * 获取适配器adapter
     */
    abstract fun getSpecialAdapter(): BaseListAdapter<ITEMBEAN, ITEMVIEW>

    /**
     * 获取presenter
     */
    abstract fun getSpecialPresenter(): BaseListPresenter

    /**
     * 从返回结果中获取列表数据集合
     */
    abstract fun getList(response: RESPONSE?): List<ITEMBEAN>?

}