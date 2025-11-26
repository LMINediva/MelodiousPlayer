package com.melodiousplayer.android.base

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R

/**
 * 所有具有下拉刷新和上拉加载更多的3列网格列表界面的基类
 * 基类抽取
 * HomeView -> BaseView
 * Adapter -> BaseListAdapter
 * Presenter -> BaseListPresenter
 */
abstract class BaseGridListFragment<RESPONSE, ITEMBEAN, ITEMVIEW : View> : BaseFragment(),
    BaseView<RESPONSE>, OnDataChangedListener {

    // 适配
    val adapter by lazy { getSpecialAdapter() }
    val presenter by lazy { getSpecialPresenter() }

    private lateinit var recyclerView: RecyclerView
    private var listener: MessageListener? = null
    private var offset: Int = 1

    override fun initView(): View? {
        val view = View.inflate(context, R.layout.fragment_my_list, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun initListener() {
        // 初始化RecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = adapter
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
                    if (layoutManager is GridLayoutManager) {
                        val manager: GridLayoutManager = layoutManager
                        val lastPosition = manager.findLastVisibleItemPosition()
                        if (lastPosition == adapter.itemCount - 1) {
                            // 最后一条已经显示了
                            presenter.loadMore(++offset)
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

    override fun loadSuccess(response: RESPONSE?) {
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