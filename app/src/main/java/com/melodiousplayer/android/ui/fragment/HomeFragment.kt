package com.melodiousplayer.android.ui.fragment

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.HomeAdapter
import com.melodiousplayer.android.base.BaseFragment
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
                println("获取数据出错：" + Thread.currentThread().name)
            }

            /**
             * 子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                println("获取数据成功：" + Thread.currentThread().name)
            }
        })
    }

}