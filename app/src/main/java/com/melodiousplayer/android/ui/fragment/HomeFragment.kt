package com.melodiousplayer.android.ui.fragment

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun initView(): View? {
        val view = View.inflate(context, R.layout.fragment_home, null)
        recyclerView = view.findViewById(R.id.recyclerView)
        return view
    }

    override fun initListener() {
        // 初始化RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
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
                // 刷新列表
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        adapter.updateList(list)
                    }
                })
            }
        })
    }

}