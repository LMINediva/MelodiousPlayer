package com.melodiousplayer.android.ui.fragment

import android.graphics.Color
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.HomeAdapter
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.util.ThreadUtil
import com.melodiousplayer.android.util.URLProviderUtils
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * 首页
 */
class HomeFragment : BaseFragment() {

    // 适配
    val adapter by lazy { HomeAdapter() }

    private lateinit var recyclerView: RecyclerView
    private lateinit var refreshLayout: SwipeRefreshLayout

    override fun initView(): View? {
        val view = View.inflate(context, R.layout.fragment_home, null)
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
            loadDatas()
        }
        // 监听列表滑动
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_IDLE -> {
                        println("idel")
                    }

                    RecyclerView.SCROLL_STATE_DRAGGING -> {
                        println("drag")
                    }

                    RecyclerView.SCROLL_STATE_SETTLING -> {
                        println("settling")
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                println("onScrolled dx=$dx dy=$dy")
            }
        })
    }

    override fun initData() {
        // 初始化数据
        loadDatas()
    }

    private fun loadDatas() {
        val path = URLProviderUtils.getHomeUrl(0, 20)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(path)
            .get()
            .build()
        client.newCall(request).enqueue(object : Callback {
            /**
             * 子线程中调用
             */
            override fun onFailure(call: Call, e: IOException) {
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        // 隐藏刷新控件
                        refreshLayout.isRefreshing = false
                    }
                })
                myToast("获取数据失败")
                Log.i("HomeFragment", "onFailure: " + e.message)
            }

            /**
             * 子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                myToast("获取数据成功")
                val result = response.body?.string()
                val gson = Gson()
                val list = gson.fromJson<List<HomeItemBean>>(
                    result,
                    object : TypeToken<List<HomeItemBean>>() {}.type
                )
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        // 隐藏刷新控件
                        refreshLayout.isRefreshing = false
                        // 刷新列表
                        adapter.updateList(list)
                    }
                })
            }
        })
    }

}