package com.melodiousplayer.android.ui.fragment

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.melodiousplayer.android.base.BaseFragment

/**
 * MV界面每一个页面的fragment
 */
class MVPagerFragment : BaseFragment() {

    // 在fragment创建时传递数据，不能通过构造方法，需要通过setArguments方法传递
    var name: String? = null

    override fun init() {
        // 获取传递的数据
        name = arguments?.getString("args")
    }

    override fun initView(): View? {
        val tv = TextView(context)
        tv.gravity = Gravity.CENTER
        tv.setTextColor(Color.RED)
        tv.text = javaClass.simpleName + name
        return tv
    }

}