package com.melodiousplayer.android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import splitties.toast.toast

/**
 * 所有fragment的基类
 */
abstract class BaseFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    /**
     * fragment初始化
     */
    protected fun init() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return initView()
    }

    /**
     * 获取布局view
     */
    abstract fun initView(): View?

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initListener()
        initData()
    }

    /**
     * 数据的初始化
     */
    protected fun initData() {

    }

    /**
     * adapter listener
     */
    protected fun initListener() {

    }

    /**
     * 实现可在子线程中安全的弹出消息
     */
    fun myToast(msg: String) {
        // 通过父Activity来调用runOnUiThread
        activity?.runOnUiThread { toast(msg) }
    }

}