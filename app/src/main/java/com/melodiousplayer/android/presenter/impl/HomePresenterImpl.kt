package com.melodiousplayer.android.presenter.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.melodiousplayer.android.model.HomeItemBean
import com.melodiousplayer.android.presenter.interf.HomePresenter
import com.melodiousplayer.android.util.ThreadUtil
import com.melodiousplayer.android.util.URLProviderUtils
import com.melodiousplayer.android.view.HomeView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class HomePresenterImpl(var homeView: HomeView) : HomePresenter {

    /**
     * 初始化数据或者刷新数据
     */
    override fun loadDatas() {
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
                        // 回调到view层处理
                        homeView.onError(e.message)
                    }
                })
            }

            /**
             * 子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val gson = Gson()
                val list = gson.fromJson<List<HomeItemBean>>(
                    result,
                    object : TypeToken<List<HomeItemBean>>() {}.type
                )
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        // 将正确的结果回调到view层
                        homeView.loadSuccess(list)
                    }
                })
            }
        })
    }

    /**
     * 加载更多数据
     */
    override fun loadMore(offset: Int) {
        val path = URLProviderUtils.getHomeUrl(offset, 20)
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
                        // 回调到view层处理
                        homeView.onError(e.message)
                    }
                })
            }

            /**
             * 子线程中调用
             */
            override fun onResponse(call: Call, response: Response) {
                val result = response.body?.string()
                val gson = Gson()
                val list = gson.fromJson<List<HomeItemBean>>(
                    result,
                    object : TypeToken<List<HomeItemBean>>() {}.type
                )
                ThreadUtil.runOnMainThread(object : Runnable {
                    override fun run() {
                        homeView.loadMore(list)
                    }
                })
            }
        })
    }

}