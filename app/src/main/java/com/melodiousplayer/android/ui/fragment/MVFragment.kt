package com.melodiousplayer.android.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.MVPagerAdapter
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.presenter.impl.MVPresenterImpl
import com.melodiousplayer.android.ui.activity.MainActivity
import com.melodiousplayer.android.view.MVView

/**
 * MV
 */
class MVFragment : BaseFragment(), MVView {

    private lateinit var view: View
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    val presenter by lazy { MVPresenterImpl(this) }

    companion object {
        /**
         * 单例，返回此片段的新实例
         */
        @JvmStatic
        fun newInstance(): MVFragment {
            return MVFragment()
        }
    }

    override fun initView(): View? {
        if (!::view.isInitialized) {
            view = View.inflate(context, R.layout.fragment_mv, null)
        }
        return view
    }

    override fun initListener() {

    }

    override fun initData() {
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        // 加载区域数据
        presenter.loadDatas()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        (context as MainActivity).onMVFragmentAdded()
    }

    override fun onError(msg: String?) {
        myToast("加载区域数据失败")
    }

    override fun onSuccess(result: List<MVAreaBean>) {
        // 在fragment中管理fragment需要用childFragmentManager
        val adapter = context?.let { MVPagerAdapter(it, result, childFragmentManager) }
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }
}