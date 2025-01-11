package com.melodiousplayer.android.ui.fragment

import android.view.View
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.MVPagerAdapter
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.presenter.impl.MVPresenterImpl
import com.melodiousplayer.android.view.MVView

/**
 * MV
 */
class MVFragment : BaseFragment(), MVView {

    val presenter by lazy { MVPresenterImpl(this) }

    override fun initView(): View? {
        return View.inflate(context, R.layout.fragment_mv, null)
    }

    override fun initListener() {

    }

    override fun initData() {
        // 加载区域数据
        presenter.loadDatas()
    }

    override fun onError(msg: String?) {
        myToast("加载区域数据失败")
    }

    override fun onSuccess(result: List<MVAreaBean>) {
        // 在fragment中管理fragment需要用childFragmentManager
        val adapter = MVPagerAdapter(result, childFragmentManager)
    }

}