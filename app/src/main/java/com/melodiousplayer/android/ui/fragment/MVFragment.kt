package com.melodiousplayer.android.ui.fragment

import android.content.res.ColorStateList
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.melodiousplayer.android.R
import com.melodiousplayer.android.adapter.MVPagerAdapter
import com.melodiousplayer.android.base.BaseFragment
import com.melodiousplayer.android.model.MVAreaBean
import com.melodiousplayer.android.presenter.impl.MVPresenterImpl
import com.melodiousplayer.android.util.ThemeUtil
import com.melodiousplayer.android.view.MVView

/**
 * MV
 */
class MVFragment : BaseFragment(), MVView {

    private lateinit var view: View
    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var mvAreaLoadingLayout: RelativeLayout
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

    override fun initData() {
        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        mvAreaLoadingLayout = view.findViewById(R.id.mvAreaLoadingLayout)
        val isDarkTheme = context?.let { ThemeUtil.isDarkTheme(it) }
        if (isDarkTheme == true) {
            val indicatorColor = context?.getColor(R.color.white)
            tabLayout.setBackgroundResource(R.color.darkGrayNight)
            if (indicatorColor != null) {
                tabLayout.setSelectedTabIndicatorColor(indicatorColor)
            }
            tabLayout.tabTextColors = context?.let {
                ColorStateList.valueOf(
                    ContextCompat.getColor(
                        it,
                        R.color.darkGray
                    )
                )
            }
        }
        // 加载区域数据
        presenter.loadDatas()
    }

    override fun initListener() {

    }

    override fun onError(msg: String?) {
        myToast("加载区域数据失败")
    }

    override fun onSuccess(result: List<MVAreaBean>) {
        tabLayout.visibility = View.VISIBLE
        mvAreaLoadingLayout.visibility = View.GONE
        viewPager.visibility = View.VISIBLE
        // 在fragment中管理fragment需要用childFragmentManager
        val adapter = context?.let { MVPagerAdapter(it, result, childFragmentManager) }
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)
    }

}